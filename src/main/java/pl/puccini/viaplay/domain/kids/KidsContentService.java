package pl.puccini.viaplay.domain.kids;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreRepository;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.repository.MovieRepository;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;

import java.util.List;

@Service
public class KidsContentService {
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final GenreRepository genreRepository;

    public KidsContentService(MovieRepository movieRepository, SeriesRepository seriesRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.seriesRepository = seriesRepository;
        this.genreRepository = genreRepository;
    }

    public List<Movie> getAllKidsMovies() {
        return movieRepository.findAllByGenre(findGenreByName("Kids"));
    }


    public List<Series> getAllKidsSeries() {
        return seriesRepository.findAllByGenre(findGenreByName("Kids"));
    }

    private Genre findGenreByName(String name) {
        return genreRepository.findByGenreType(name);
    }
}
