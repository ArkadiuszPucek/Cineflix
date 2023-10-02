package pl.puccini.viaplay.domain.movie.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.imdb.IMDbApiService;
import pl.puccini.viaplay.domain.imdb.IMDbData;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.dto.MovieDtoMapper;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.repository.MovieRepository;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDtoMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final IMDbApiService imdbApiService;

    public MovieService(MovieRepository movieRepository, IMDbApiService imdbApiService) {
        this.movieRepository = movieRepository;
        this.imdbApiService = imdbApiService;
    }


    public List<MovieDto> findAllPromotedMovies() {
        return movieRepository.findAllByPromotedIsTrue().stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    public void addMovie(Movie movie) throws IOException, InterruptedException {
        // Zapisz film w bazie danych
        movieRepository.save(movie);

        // Pobierz dane IMDb na podstawie IMDb ID
        IMDbData imdbData = imdbApiService.fetchIMDbData(movie.getImdbId());

        // Zaktualizuj dane filmu na podstawie danych IMDb
        movie.setImdbRating(imdbData.getImdbRating());
        movie.setImdbUrl(imdbData.getImdbUrl());

        // Ponownie zapisz film w bazie danych, aby uwzględnić dane IMDb
        movieRepository.save(movie);
    }

    public List<MovieDto> getMoviesByImdbId(String imdbId){
        return movieRepository.findAllByImdbId(imdbId).stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    public List<MovieDto> getMovieByGenre(Genre genre){
        return movieRepository.findAllByGenre(genre).stream()
                .map(MovieDtoMapper::map)
                .toList();
    }
}
