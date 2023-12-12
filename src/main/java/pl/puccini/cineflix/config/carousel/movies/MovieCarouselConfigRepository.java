package pl.puccini.cineflix.config.carousel.movies;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.config.carousel.movies.MoviesCarouselConfig;

public interface MovieCarouselConfigRepository extends CrudRepository<MoviesCarouselConfig, Long> {
    MoviesCarouselConfig findTopByOrderByIdDesc();
}
