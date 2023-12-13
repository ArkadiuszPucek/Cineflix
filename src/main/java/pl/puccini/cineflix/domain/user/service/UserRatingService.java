package pl.puccini.cineflix.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.model.UserRating;
import pl.puccini.cineflix.domain.user.repository.UserRatingRepository;
import pl.puccini.cineflix.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
public class UserRatingService {
    private final UserRatingRepository userRatingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;


    public UserRatingService(UserRatingRepository userRatingRepository, UserRepository userRepository, MovieRepository movieRepository, SeriesRepository seriesRepository) {
        this.userRatingRepository = userRatingRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.seriesRepository = seriesRepository;
    }

    @Transactional
    public void rateItem(String imdbId, Long userId, boolean isUpvote) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o ID: " + userId));

        Movie movie = movieRepository.findMovieByImdbId(imdbId);
        if (movie != null) {
            processRating(user, movie, null, isUpvote);
            return;
        }

        Series series = seriesRepository.findByImdbId(imdbId);
        if (series != null) {
            processRating(user, null, series, isUpvote);
            return;
        }

        throw new IllegalArgumentException("Nie znaleziono filmu ani serialu o imdbId: " + imdbId);
    }

    private void processRating(User user, Movie movie, Series series, boolean isUpvote) {
        Optional<UserRating> existingRatingOpt = movie != null ?
                userRatingRepository.findByUserAndMovie(user, movie) :
                userRatingRepository.findByUserAndSeries(user, series);

        if (existingRatingOpt.isPresent()) {
            UserRating existingRating = existingRatingOpt.get();
            if (existingRating.isUpvote() == isUpvote) {
                userRatingRepository.delete(existingRating);
            } else {
                existingRating.setUpvote(isUpvote);
                userRatingRepository.save(existingRating);
            }
        } else {
            UserRating newRating = new UserRating();
            newRating.setUser(user);
            if (movie != null) newRating.setMovie(movie);
            if (series != null) newRating.setSeries(series);
            newRating.setUpvote(isUpvote);
            userRatingRepository.save(newRating);
        }
    }
    public Optional<Boolean> getCurrentUserRatingForMovie(String imdbId, Long userId) {
        return userRatingRepository.findByMovieImdbIdAndUserId(imdbId, userId)
                .map(UserRating::isUpvote);
    }

    public Optional<Boolean> getCurrentUserRatingForSeries(String imdbId, Long userId) {
        return userRatingRepository.findBySeriesImdbIdAndUserId(imdbId, userId)
                .map(UserRating::isUpvote);
    }

}
