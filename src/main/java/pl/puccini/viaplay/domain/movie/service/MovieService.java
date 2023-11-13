package pl.puccini.viaplay.domain.movie.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreRepository;
import pl.puccini.viaplay.domain.imdb.IMDbApiService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.dto.MovieDtoMapper;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.repository.MovieRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final IMDbApiService imdbApiService;
    private final GenreRepository genreRepository;

    public MovieService(MovieRepository movieRepository, IMDbApiService imdbApiService, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.imdbApiService = imdbApiService;
        this.genreRepository = genreRepository;
    }


    public List<MovieDto> findAllPromotedMovies() {
        return movieRepository.findAllByPromotedIsTrue().stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    public void addMovieManual(MovieDto movieDto) throws IOException, InterruptedException {
        Movie movie = new Movie();
        movie.setImdbId(movieDto.getImdbId());
        movie.setTitle(movieDto.getTitle());
        movie.setReleaseYear(movieDto.getReleaseYear());
        movie.setImageUrl(movieDto.getImageUrl());
        movie.setBackgroundImageUrl(movieDto.getBackgroundImageUrl());
        movie.setMediaUrl(movieDto.getMediaUrl());
        movie.setTimeline(movieDto.getTimeline());
        movie.setAgeLimit(movieDto.getAgeLimit());
        movie.setDescription(movieDto.getDescription());
        movie.setStaff(movieDto.getStaff());
        movie.setDirectedBy(movieDto.getDirectedBy());
        movie.setLanguages(movieDto.getLanguages());
        movie.setGenre(genreRepository.findByGenreTypeIgnoreCase(movieDto.getGenre()));
        movie.setImdbRating(movieDto.getImdbRating());
        movie.setPromoted(movieDto.isPromoted());
        movie.setImdbUrl("https://www.imdb.com/title/"+movieDto.getImdbId());
        movieRepository.save(movie);
    }

    public Movie addMovieByApi(MovieDto movieDto) throws IOException, InterruptedException {
        MovieDto movieApiDto = imdbApiService.fetchIMDbData(movieDto.getImdbId());
        Movie movie = new Movie();
        movie.setImdbId(movieApiDto.getImdbId());
        movie.setTitle(movieApiDto.getTitle());
        movie.setReleaseYear(movieApiDto.getReleaseYear());
        movie.setImageUrl(movieApiDto.getImageUrl());
        movie.setBackgroundImageUrl(movieApiDto.getBackgroundImageUrl());
        movie.setMediaUrl(movieApiDto.getMediaUrl());
        movie.setTimeline(movieApiDto.getTimeline());
        movie.setAgeLimit(movieApiDto.getAgeLimit());
        movie.setDescription(movieApiDto.getDescription());
        movie.setStaff(movieApiDto.getStaff());
        movie.setDirectedBy(movieApiDto.getDirectedBy());
        movie.setLanguages(movieApiDto.getLanguages());
        movie.setGenre(genreRepository.findByGenreTypeIgnoreCase(movieApiDto.getGenre()));
        movie.setImdbRating(movieApiDto.getImdbRating());
        movie.setPromoted(movieDto.isPromoted());
        movie.setImdbUrl("https://www.imdb.com/title/"+movieDto.getImdbId());
        movieRepository.save(movie);
        return movie;
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

    public MovieDto findMovieByTitle(String title) {
        Movie movie = movieRepository.findByTitleIgnoreCase(title);
        if (movie == null){
            return null;
        }
        return MovieDtoMapper.map(movie);
    }

    public List<MovieDto> searchMovies(String query) {
        String loweredQuery = query.toLowerCase();
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        return movieRepository.findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCaseOrDirectedByContainingIgnoreCase(loweredQuery , loweredQuery , loweredQuery).stream()
                .map(MovieDtoMapper::map)
                .toList();
    }



}
