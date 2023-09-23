package pl.puccini.viaplay.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.series.model.Episode;

import java.util.List;

public interface EpisodeRepository extends CrudRepository<Episode, Long> {
    List<Episode> findAllBy();
}
