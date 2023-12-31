package pl.puccini.cineflix.domain.genre.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pl.puccini.cineflix.domain.genre.model.Genre;

import java.util.List;

public interface GenreRepository extends CrudRepository<Genre, Long> {
    List<Genre> findAll();

    Genre findByGenreType(String genreType);
    Genre findByGenreTypeIgnoreCase(String genreType);
    @Query("SELECT g FROM Genre g WHERE (SELECT COUNT(s) FROM Series s WHERE s.genre = g) >= :minSeriesCount")
    List<Genre> findGenresWithMinimumSeries(@Param("minSeriesCount") int minSeriesCount);
    @Query("SELECT g FROM Genre g WHERE (SELECT COUNT(s) FROM Movie s WHERE s.genre = g) >= :minMoviesCount")
    List<Genre> findGenresWithMinimumMovie(int minMoviesCount);
}
