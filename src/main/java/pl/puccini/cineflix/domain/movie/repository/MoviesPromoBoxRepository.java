package pl.puccini.cineflix.domain.movie.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.model.MoviesPromoBox;
import pl.puccini.cineflix.domain.series.model.SeriesPromoBox;

import java.util.List;

public interface MoviesPromoBoxRepository extends CrudRepository<MoviesPromoBox, Long> {
    List<MoviesPromoBox> findAll();
    MoviesPromoBox findTopByOrderByIdDesc();

}
