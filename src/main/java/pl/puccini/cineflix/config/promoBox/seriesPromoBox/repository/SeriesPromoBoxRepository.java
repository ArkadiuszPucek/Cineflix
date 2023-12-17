package pl.puccini.cineflix.config.promoBox.seriesPromoBox.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.config.promoBox.seriesPromoBox.model.SeriesPromoBox;

import java.util.List;

public interface SeriesPromoBoxRepository extends CrudRepository<SeriesPromoBox, Long> {
    List<SeriesPromoBox> findAll();
    SeriesPromoBox findTopByOrderByIdDesc();
}
