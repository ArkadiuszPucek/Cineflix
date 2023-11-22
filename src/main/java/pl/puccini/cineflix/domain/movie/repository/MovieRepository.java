package pl.puccini.cineflix.domain.movie.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.movie.model.Movie;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findAllByPromotedIsTrue();
    List<Movie> findAllByImdbId(String imdbId);
    List<Movie> findAllByGenre(Genre genre);
    List<Movie> findAll();
    Movie findByTitleIgnoreCase(String title);
    Movie findMovieByImdbId(String imdbId);

    List<Movie> findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCaseOrDirectedByContainingIgnoreCase(String title, String actor, String director);
    boolean existsByImdbId(String imdbId);



}
