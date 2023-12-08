package pl.puccini.cineflix.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.series.model.Episode;
import pl.puccini.cineflix.domain.series.model.SeriesCarouselConfig;

import java.util.List;

public interface EpisodeRepository extends CrudRepository<Episode, Long> {
    List<Episode> findAllByOrderBySeason();
    List<Episode> findAllBy();

    List<Episode> findBySeasonId(Long seasonId);

    Episode findEpisodeById(Long episodeId);

}
