package pl.puccini.viaplay.domain.genre;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GenreRepository extends CrudRepository<Genre, Long> {
    List<Genre> findAll();

    Genre findByGenreType(String genreType);
    Genre findByGenreTypeIgnoreCase(String genreType);


}
