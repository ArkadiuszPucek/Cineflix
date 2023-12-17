package pl.puccini.cineflix.domain.user.userRatings.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.exceptions.MovieNotFoundException;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userRatings.model.UserRating;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.userRatings.repository.UserRatingRepository;

import java.util.Optional;

@Service
public class UserMovieRatingService {
    private final UserRatingRepository userRatingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public UserMovieRatingService(UserRatingRepository userRatingRepository, UserRepository userRepository, MovieRepository movieRepository) {
        this.userRatingRepository = userRatingRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public void rateMovie(String imdbId, Long userId, boolean isUpvote) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Movie movie = movieRepository.findMovieByImdbId(imdbId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with IMDb ID: " + imdbId));

        processRating(user, movie, isUpvote);
    }

    private void processRating(User user, Movie movie, boolean isUpvote) {
        Optional<UserRating> existingRatingOpt = userRatingRepository.findByUserAndMovie(user, movie);

        existingRatingOpt.ifPresentOrElse(existingRating -> {
            if (existingRating.isUpvote() == isUpvote) {
                userRatingRepository.delete(existingRating);
            } else {
                existingRating.setUpvote(isUpvote);
                userRatingRepository.save(existingRating);
            }
        }, () -> {
            UserRating newRating = new UserRating();
            newRating.setUser(user);
            newRating.setMovie(movie);
            newRating.setUpvote(isUpvote);
            userRatingRepository.save(newRating);
        });
    }

    public Optional<Boolean> getCurrentUserRatingForMovie(String imdbId, Long userId) {
        return userRatingRepository.findByMovieImdbIdAndUserId(imdbId, userId)
                .map(UserRating::isUpvote);
    }

    public int getRateUpCountForMovies(Movie movie) {
        return userRatingRepository.countByMovieImdbIdAndUpvote(movie.getImdbId(), true);
    }

    public int getRateDownCountForMovies(Movie movie) {
        return userRatingRepository.countByMovieImdbIdAndUpvote(movie.getImdbId(), false);
    }
}
