package pl.puccini.viaplay.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.series.model.Season;

import java.util.List;

public interface SeasonRepository extends CrudRepository<Season, Long> {
    List<Season> findAllBy();
}
