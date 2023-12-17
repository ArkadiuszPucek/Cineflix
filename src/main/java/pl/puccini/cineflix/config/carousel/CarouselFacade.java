package pl.puccini.cineflix.config.carousel;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.config.carousel.movies.service.MovieCarouselService;
import pl.puccini.cineflix.config.carousel.series.service.SeriesCarouselService;

import java.util.List;

@Service
public class CarouselFacade {
    private final SeriesCarouselService seriesCarouselService;
    private final MovieCarouselService movieCarouselService;

    public CarouselFacade(SeriesCarouselService seriesCarouselService, MovieCarouselService movieCarouselService) {
        this.seriesCarouselService = seriesCarouselService;
        this.movieCarouselService = movieCarouselService;
    }

    public void saveSelectedGenresForMovies(List<String> selectedGenres) {
        movieCarouselService.saveSelectedGenres(selectedGenres);
    }
    public List<String> getSelectedGenresForMovies() {
        return movieCarouselService.getSelectedGenres();
    }
    public void saveSelectedGenresForSeries(List<String> selectedGenres) {
        seriesCarouselService.saveSelectedGenres(selectedGenres);
    }
    public List<String> getSelectedGenresForSeries() {
        return seriesCarouselService.getSelectedGenres();
    }

}
