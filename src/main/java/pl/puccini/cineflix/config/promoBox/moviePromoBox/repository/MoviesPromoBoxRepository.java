package pl.puccini.cineflix.config.promoBox.moviePromoBox.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.config.promoBox.moviePromoBox.model.MoviesPromoBox;

import java.util.List;

public interface MoviesPromoBoxRepository extends CrudRepository<MoviesPromoBox, Long> {
    List<MoviesPromoBox> findAll();
    MoviesPromoBox findTopByOrderByIdDesc();

}
