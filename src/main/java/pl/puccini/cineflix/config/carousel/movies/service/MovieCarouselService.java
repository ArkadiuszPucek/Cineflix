package pl.puccini.cineflix.config.carousel.movies.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.config.carousel.CarouselConfigService;
import pl.puccini.cineflix.config.carousel.movies.model.MoviesCarouselConfig;
import pl.puccini.cineflix.config.carousel.movies.repository.MovieCarouselConfigRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MovieCarouselService implements CarouselConfigService<MoviesCarouselConfig> {
private final MovieCarouselConfigRepository movieCarouselConfigRepository;

    public MovieCarouselService(MovieCarouselConfigRepository movieCarouselConfigRepository) {
        this.movieCarouselConfigRepository = movieCarouselConfigRepository;
    }

    @Override
    public void saveSelectedGenres(List<String> selectedGenres) {
        MoviesCarouselConfig config = getConfigById(1L);
        String joinedGenres = selectedGenres.isEmpty() ? "" : String.join(",", selectedGenres);
        config.setActiveGenres(joinedGenres);
        movieCarouselConfigRepository.save(config);
    }

    @Override
    public List<String> getSelectedGenres() {
        MoviesCarouselConfig config = getTopByOrderByIdDesc();
        return config != null && config.getActiveGenres() != null ?
                Arrays.asList(config.getActiveGenres().split(",")) :
                new ArrayList<>();
    }

    @Override
    public MoviesCarouselConfig getConfigById(Long id) {
        return movieCarouselConfigRepository.findById(id).orElse(new MoviesCarouselConfig());
    }

    @Override
    public MoviesCarouselConfig getTopByOrderByIdDesc() {
        return movieCarouselConfigRepository.findTopByOrderByIdDesc();
    }
}