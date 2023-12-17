package pl.puccini.cineflix.domain.genre;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.genre.service.GenreService;

import java.util.List;

@Service
public class GenreFacade {
    private final GenreService genreService;

    public GenreFacade(GenreService genreService) {
        this.genreService = genreService;
    }

    public List<Genre> getAllGenres(){
        return genreService.getAllGenres();
    }

    public Genre getGenreByType(String genreType) {
        return genreService.getGenreByType(genreType);
    }

    public List<Genre> getGenresWithMinimumSeries(int minSeriesCount) {
        return genreService.getGenresWithMinimumSeries(minSeriesCount);
    }

    public List<Genre> getGenresWithMinimumMovies(int minMoviesCount) {
        return genreService.getGenresWithMinimumMovies(minMoviesCount);
    }
}
