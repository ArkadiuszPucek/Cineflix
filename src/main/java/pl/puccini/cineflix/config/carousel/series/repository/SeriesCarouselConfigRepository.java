package pl.puccini.cineflix.config.carousel.series.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.config.carousel.series.model.SeriesCarouselConfig;

public interface SeriesCarouselConfigRepository extends CrudRepository<SeriesCarouselConfig, Long> {
    SeriesCarouselConfig findTopByOrderByIdDesc();

}