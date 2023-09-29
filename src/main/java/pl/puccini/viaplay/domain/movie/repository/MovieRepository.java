package pl.puccini.viaplay.domain.movie.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.series.model.Series;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findAllByPromotedIsTrue();
    List<Movie> findAllByImdbId(String imdbId);
    List<Movie> findAllByGenreGenreType(String genre);
}
