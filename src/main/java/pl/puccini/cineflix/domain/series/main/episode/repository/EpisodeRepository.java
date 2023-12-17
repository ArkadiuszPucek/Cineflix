package pl.puccini.cineflix.domain.series.main.episode.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends CrudRepository<Episode, Long> {
    List<Episode> findAllByOrderBySeason();
    List<Episode> findAllBy();

    List<Episode> findBySeasonId(Long seasonId);

    @Override
    Optional<Episode> findById(Long episodeId);

    Episode findEpisodeById(Long episodeId);

}
