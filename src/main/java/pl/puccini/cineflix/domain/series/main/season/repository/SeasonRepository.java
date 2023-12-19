package pl.puccini.cineflix.domain.series.main.season.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.series.main.season.model.Season;
import pl.puccini.cineflix.domain.series.main.series.model.Series;

import java.util.List;

public interface SeasonRepository extends CrudRepository<Season, Long> {
   List<Season> findSeasonsBySeriesImdbId(String imdbId);
    Season findBySeriesAndSeasonNumber(Series series, int seasonNumber);


}
