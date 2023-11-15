package pl.puccini.viaplay.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.series.model.Season;
import pl.puccini.viaplay.domain.series.model.Series;

import java.util.List;
import java.util.Optional;

public interface SeasonRepository extends CrudRepository<Season, Long> {
    List<Season> findAllBy();

    List<Season> findSeasonsBySeriesImdbId(String imdbId);
    Season findBySeriesAndSeasonNumber(Series series, int seasonNumber);


}
