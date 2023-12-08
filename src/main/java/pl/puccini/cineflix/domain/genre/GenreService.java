package pl.puccini.cineflix.domain.genre;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.movie.model.MoviesCarouselConfig;
import pl.puccini.cineflix.domain.movie.repository.MovieCarouselConfigRepository;
import pl.puccini.cineflix.domain.series.model.SeriesCarouselConfig;
import pl.puccini.cineflix.domain.series.repository.SeriesCarouselConfigRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final SeriesCarouselConfigRepository seriesCarouselConfigRepository;
    private final MovieCarouselConfigRepository movieCarouselConfigRepository;

    public GenreService(GenreRepository genreRepository, SeriesCarouselConfigRepository seriesCarouselConfigRepository, MovieCarouselConfigRepository movieCarouselConfigRepository) {
        this.genreRepository = genreRepository;
        this.seriesCarouselConfigRepository = seriesCarouselConfigRepository;
        this.movieCarouselConfigRepository = movieCarouselConfigRepository;
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

    public void saveSelectedSeriesGenres(List<String> selectedGenres) {
        SeriesCarouselConfig config = seriesCarouselConfigRepository.findById(1L).orElse(new SeriesCarouselConfig());
        if (selectedGenres.isEmpty()) {
            config.setActiveGenres("");
        } else {
            String joinedGenres = String.join(",", selectedGenres);
            config.setActiveGenres(joinedGenres);
        }
        seriesCarouselConfigRepository.save(config);
    }

    public List<String> getSeriesSelectedGenres() {
        SeriesCarouselConfig config = seriesCarouselConfigRepository.findTopByOrderByIdDesc();
        if (config != null && config.getActiveGenres() != null) {
            return Arrays.asList(config.getActiveGenres().split(","));
        }
        return new ArrayList<>();
    }

    public List<String> getMoviesSelectedGenres() {
        MoviesCarouselConfig config = movieCarouselConfigRepository.findTopByOrderByIdDesc();
        if (config != null && config.getActiveGenres() != null) {
            return Arrays.asList(config.getActiveGenres().split(","));
        }
        return new ArrayList<>();
    }

    public void saveSelectedMoviesGenres(List<String> selectedGenres) {
        MoviesCarouselConfig config = movieCarouselConfigRepository.findById(1L).orElse(new MoviesCarouselConfig());
        if (selectedGenres.isEmpty()) {
            config.setActiveGenres("");
        } else {
            String joinedGenres = String.join(",", selectedGenres);
            config.setActiveGenres(joinedGenres);
        }
        movieCarouselConfigRepository.save(config);
    }

}
