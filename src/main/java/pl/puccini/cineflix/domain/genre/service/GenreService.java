package pl.puccini.cineflix.domain.genre.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.genre.repository.GenreRepository;

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
