package pl.puccini.cineflix.domain.kids;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreRepository;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.series.service.SeriesService;

import java.util.List;

@Service
public class KidsContentService {
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final MovieService movieService;
    private final SeriesService seriesService;
    private final GenreRepository genreRepository;
    private static final String KIDS_GENRE = "Kids";

    public KidsContentService(MovieRepository movieRepository, SeriesRepository seriesRepository, MovieService movieService, SeriesService seriesService, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.seriesRepository = seriesRepository;
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.genreRepository = genreRepository;
    }

    public List<MovieDto> getAllKidsMovies(Long userId) {
        return movieService.getMovieByGenre(KIDS_GENRE, userId);
    }


    public List<SeriesDto> getAllKidsSeries(Long userId) {
        return seriesService.getSeriesByGenre(KIDS_GENRE, userId);
//        return seriesRepository.findAllByGenre(findGenreByName("Kids"));
    }

//    private Genre findGenreByName(String name) {
//        return genreRepository.findByGenreType(name);
//    }
}
