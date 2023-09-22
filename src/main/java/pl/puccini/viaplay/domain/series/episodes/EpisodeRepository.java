package pl.puccini.viaplay.domain.series.episodes;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EpisodeRepository extends CrudRepository<Episode, Long> {
    List<Episode> findAllBy();
}
