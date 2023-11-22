package pl.puccini.cineflix.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.series.model.Season;
import pl.puccini.cineflix.domain.series.model.Series;

import java.util.List;

public interface SeasonRepository extends CrudRepository<Season, Long> {
    List<Season> findAllBy();

    List<Season> findSeasonsBySeriesImdbId(String imdbId);
    Season findBySeriesAndSeasonNumber(Series series, int seasonNumber);


}
