package pl.puccini.viaplay.domain.series;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.movie.Movie;

import java.util.List;

public interface SeriesRepository extends CrudRepository<Series, Long> {
    List<Series> findAllByPromotedIsTrue();
}
