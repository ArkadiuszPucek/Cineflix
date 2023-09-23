package pl.puccini.viaplay.domain.movie.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.movie.model.Movie;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findAllByPromotedIsTrue();
}
