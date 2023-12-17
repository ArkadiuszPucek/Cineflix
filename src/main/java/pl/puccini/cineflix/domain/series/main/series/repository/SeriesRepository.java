package pl.puccini.cineflix.domain.series.main.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.series.main.series.model.Series;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends CrudRepository<Series, Long> {
    List<Series> findAllByPromotedIsTrue();
    List<Series> findAll();
    List<Series> findAllByGenre(Genre genre);
    Series findByTitleIgnoreCase(String title);
    List<Series> findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCase(String title, String actor);
    boolean existsByImdbId(String imdbId);
    Optional<Series> findSeriesByImdbId(String imdbId);
}
