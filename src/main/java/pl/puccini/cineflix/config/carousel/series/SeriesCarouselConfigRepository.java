package pl.puccini.cineflix.config.carousel.series;

import org.springframework.data.repository.CrudRepository;

public interface SeriesCarouselConfigRepository extends CrudRepository<SeriesCarouselConfig, Long> {
    SeriesCarouselConfig findTopByOrderByIdDesc();

}