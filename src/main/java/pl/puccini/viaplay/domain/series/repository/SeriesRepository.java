package pl.puccini.viaplay.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.series.model.Series;

import java.util.List;

public interface SeriesRepository extends CrudRepository<Series, Long> {
    List<Series> findAllByPromotedIsTrue();
}
