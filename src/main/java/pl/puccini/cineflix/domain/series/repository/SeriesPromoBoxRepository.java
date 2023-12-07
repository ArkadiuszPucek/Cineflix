package pl.puccini.cineflix.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.series.model.SeriesPromoBox;

import java.util.List;

public interface SeriesPromoBoxRepository extends CrudRepository<SeriesPromoBox, Long> {
    List<SeriesPromoBox> findAll();
    SeriesPromoBox findTopByOrderByIdDesc();
}
