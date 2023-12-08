package pl.puccini.cineflix.domain.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.series.model.SeriesCarouselConfig;

public interface SeriesCarouselConfigRepository extends CrudRepository<SeriesCarouselConfig, Long> {
    SeriesCarouselConfig findTopByOrderByIdDesc();

}