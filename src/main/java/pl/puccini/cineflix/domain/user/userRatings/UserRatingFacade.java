package pl.puccini.cineflix.domain.user.userRatings;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.user.userRatings.service.UserMovieRatingService;
import pl.puccini.cineflix.domain.user.userRatings.service.UserSeriesRatingService;

import java.util.Optional;

@Service
public class UserRatingFacade {
    private final UserMovieRatingService userMovieRatingService;
    private final UserSeriesRatingService userSeriesRatingService;

    public UserRatingFacade(UserMovieRatingService userMovieRatingService, UserSeriesRatingService userSeriesRatingService) {
        this.userMovieRatingService = userMovieRatingService;
        this.userSeriesRatingService = userSeriesRatingService;
    }

    public void rateMovie(String imdbId, Long userId, boolean isUpvote) {
        userMovieRatingService.rateMovie(imdbId, userId, isUpvote);
    }

    public Optional<Boolean> getCurrentUserRatingForMovie(String imdbId, Long userId) {
        return userMovieRatingService.getCurrentUserRatingForMovie(imdbId, userId);
    }

    public int getRateUpCountForMovies(Movie movie) {
        return userMovieRatingService.getRateUpCountForMovies(movie);
    }

    public int getRateDownCountForMovies(Movie movie) {
        return userMovieRatingService.getRateDownCountForMovies(movie);
    }


    public void rateSeries(String imdbId, Long userId, boolean isUpvote) {
        userSeriesRatingService.rateSeries(imdbId, userId, isUpvote);
    }

    public Optional<Boolean> getCurrentUserRatingForSeries(String imdbId, Long userId) {
        return userSeriesRatingService.getCurrentUserRatingForSeries(imdbId, userId);
    }

    public int getRateUpCountForSeries(Series series) {
        return userSeriesRatingService.getRateUpCountForSeries(series);
    }

    public int getRateDownCountForSeries(Series series) {
        return userSeriesRatingService.getRateDownCountForSeries(series);
    }

}
