package pl.puccini.cineflix.domain.genre;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.config.carousel.movies.MoviesCarouselConfig;
import pl.puccini.cineflix.config.carousel.movies.MovieCarouselConfigRepository;
import pl.puccini.cineflix.config.carousel.series.SeriesCarouselConfig;
import pl.puccini.cineflix.config.carousel.series.SeriesCarouselConfigRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;


    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;

    }
    public List<Genre> getAllGenres(){
        return genreRepository.findAll();
    }

    public Genre getGenreByType(String genreType) {
        return genreRepository.findByGenreType(genreType);
    }

    public List<Genre> getGenresWithMinimumSeries(int minSeriesCount) {
        return genreRepository.findGenresWithMinimumSeries(minSeriesCount);
    }

    public List<Genre> getGenresWithMinimumMovies(int minMoviesCount) {
        return genreRepository.findGenresWithMinimumMovie(minMoviesCount);
    }
}
