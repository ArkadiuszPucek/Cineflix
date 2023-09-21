package pl.puccini.viaplay.domain.movie;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findAllByPromotedIsTrue();
}
