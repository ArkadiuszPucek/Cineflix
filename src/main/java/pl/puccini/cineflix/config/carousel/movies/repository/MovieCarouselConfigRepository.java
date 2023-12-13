package pl.puccini.cineflix.config.carousel.movies.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.config.carousel.movies.model.MoviesCarouselConfig;

public interface MovieCarouselConfigRepository extends CrudRepository<MoviesCarouselConfig, Long> {
    MoviesCarouselConfig findTopByOrderByIdDesc();
}
