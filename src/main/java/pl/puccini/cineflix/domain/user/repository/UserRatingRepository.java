package pl.puccini.cineflix.domain.user.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.model.UserRating;

import java.util.Optional;

public interface UserRatingRepository extends CrudRepository<UserRating, Long> {
    Optional<UserRating> findByUserAndMovie(User user, Movie movie);

    Optional<UserRating> findByUserAndSeries(User user, Series series);

    Optional<UserRating> findByMovieImdbIdAndUserId(String imdbId, Long userId);

    Optional<UserRating> findBySeriesImdbIdAndUserId(String imdbId, Long userId);
}
