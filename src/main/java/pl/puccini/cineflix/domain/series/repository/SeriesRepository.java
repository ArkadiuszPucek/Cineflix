package pl.puccini.cineflix.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.series.model.Series;

import java.util.List;

public interface SeriesRepository extends CrudRepository<Series, Long> {
    List<Series> findAllByPromotedIsTrue();

    List<Series> findAllByImdbId(String imdbId);
    Series findByImdbId(String imdbId);
    List<Series> findAll();


    List<Series> findAllByGenre(Genre genre);

    Series findByTitleIgnoreCase(String title);

    List<Series> findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCase(String title, String actor);


    boolean existsByImdbId(String imdbId);

    Series findSeriesByImdbId(String imdbId);
}
