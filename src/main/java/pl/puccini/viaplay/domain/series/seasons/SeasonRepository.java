package pl.puccini.viaplay.domain.series.seasons;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SeasonRepository extends CrudRepository<Season, Long> {
    List<Season> findAllBy();
}
