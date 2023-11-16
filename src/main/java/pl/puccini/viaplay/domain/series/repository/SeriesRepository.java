package pl.puccini.viaplay.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.model.Series;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends CrudRepository<Series, Long> {
    List<Series> findAllByPromotedIsTrue();

    List<Series> findAllByImdbId(String imdbId);
    Series findByImdbId(String imdbId);
    List<Series> findAll();


    List<Series> findAllByGenre(Genre genre);

    Series findByTitleIgnoreCase(String title);

    List<Series> findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCase(String title, String actor);


    boolean existsByImdbId(String imdbId);
}
