package pl.puccini.viaplay.domain.movie.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.series.model.Series;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findAllByPromotedIsTrue();
    List<Movie> findAllByImdbId(String imdbId);
    List<Movie> findAllByGenre(Genre genre);
    List<Movie> findAll();
    Movie findByTitleIgnoreCase(String title);
    Movie findMovieByImdbId(String imdbId);

    List<Movie> findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCaseOrDirectedByContainingIgnoreCase(String title, String actor, String director);
    boolean existsByImdbId(String imdbId);



}
