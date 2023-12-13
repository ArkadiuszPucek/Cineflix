package pl.puccini.cineflix.domain.movie.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.*;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.movie.MovieFactory;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.dto.MovieDtoMapper;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.movie.repository.MoviesPromoBoxRepository;
import pl.puccini.cineflix.domain.user.repository.UserRatingRepository;
import pl.puccini.cineflix.domain.user.service.UserListService;
import pl.puccini.cineflix.domain.user.service.UserRatingService;

import java.io.IOException;
import java.util.*;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final IMDbApiService imdbApiService;
    private final GenreService genreService;
    private final UserRatingRepository userRatingRepository;
    private final UserRatingService userRatingService;
    private final MovieFactory movieFactory;
    private final UserListService userListService;

    public MovieService(MovieRepository movieRepository, IMDbApiService imdbApiService, GenreService genreService, UserRatingRepository userRatingRepository, MoviesPromoBoxRepository moviesPromoBoxRepository, UserRatingService userRatingService, MovieFactory movieFactory, UserListService userListService) {
        this.movieRepository = movieRepository;
        this.imdbApiService = imdbApiService;
        this.genreService = genreService;
        this.userRatingRepository = userRatingRepository;
        this.userRatingService = userRatingService;
        this.movieFactory = movieFactory;
        this.userListService = userListService;
    }

    public Movie addMovieByApiIfNotExist(MovieDto movieDto) throws IOException, InterruptedException {
        if (existsByImdbId(movieDto.getImdbId())) {
            throw new MovieAlreadyExistsException("The movie with the given IMDb id exists on the website!");
        }
        return addMovieByApi(movieDto);
    }

    public boolean existsByImdbId(String imdbId) {
        return movieRepository.existsByImdbId(imdbId);
    }

    public void addMovieManual(MovieDto movieDto) {
        Movie movie = movieFactory.createMovie(movieDto, null);
        movieRepository.save(movie);
    }

    public Movie addMovieByApi(MovieDto movieDto) throws IOException, InterruptedException {
        String type = imdbApiService.fetchIMDbForTypeCheck(movieDto.getImdbId());
        if (type.equals("movie")){
            MovieDto movieApiDto = imdbApiService.fetchIMDbDataForMovies(movieDto.getImdbId());
            Movie movie = movieFactory.createMovie(movieApiDto, movieDto.isPromoted());
            movieRepository.save(movie);
            return movie;
        }else {
            throw new IncorrectTypeException("Incorrect imdbId type for movie");
        }
    }

    public boolean updateMovie(MovieDto movieDto) {
        Movie existingMovie = movieRepository.findMovieByImdbId(movieDto.getImdbId());
        if (existingMovie != null) {
            movieFactory.updateMovieWithDto(existingMovie, movieDto);
            movieRepository.save(existingMovie);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteMovieByImdbId(String imdbId) {
        Movie movieByImdbId = movieRepository.findMovieByImdbId(imdbId);
        if (movieByImdbId != null) {
            movieRepository.delete(movieByImdbId);
            return true;
        }
        return false;
    }

    public List<MovieDto> getMovieByGenre(String genre, Long userId){
        Genre genreByType = genreService.getGenreByType(genre);
        List<MovieDto> moviesDtos = movieRepository.findAllByGenre(genreByType).stream()
                .map(MovieDtoMapper::map)
                .toList();
        moviesDtos.forEach(movie -> {
            movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId()));
            movie.setUserRating(userRatingService.getCurrentUserRatingForMovie(movie.getImdbId(), userId).orElse(null));
        });
        return moviesDtos;
    }

    public MovieDto findMovieByTitle(String title, Long userId) {
        Movie movie = movieRepository.findByTitleIgnoreCase(title);
        if (movie == null){
            return null;
        }
        MovieDto mappedMovie = MovieDtoMapper.map(movie);
        mappedMovie.setOnUserList(userListService.isOnList(userId, mappedMovie.getImdbId()));
        mappedMovie.setUserRating(userRatingService.getCurrentUserRatingForMovie(mappedMovie.getImdbId(), userId).orElse(null));
        return mappedMovie;
    }

    public List<MovieDto> findAllMoviesInService(Long userId){
        List<MovieDto> allMoviesDto = movieRepository.findAll().stream()
                .map(movie -> {
                    MovieDto movieDto = MovieDtoMapper.map(movie);
                    int rateUpCount = userRatingRepository.countByMovieImdbIdAndUpvote(movie.getImdbId(), true);
                    int rateDownCount = userRatingRepository.countByMovieImdbIdAndUpvote(movie.getImdbId(), false);
                    movieDto.setRateUpCount(rateUpCount);
                    movieDto.setRateDownCount(rateDownCount);
                    return movieDto;
                })
                .toList();

        allMoviesDto.forEach(movie -> {
            movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId()));
            movie.setUserRating(userRatingService.getCurrentUserRatingForMovie(movie.getImdbId(), userId).orElse(null));
        });
        return allMoviesDto;
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

    public MovieDto findMovieByImdbId(String imdbId){
        Movie movieByImdbId = movieRepository.findMovieByImdbId(imdbId);
        return MovieDtoMapper.map(movieByImdbId);
    }

    public Movie getMovieByImdbId(String imdbId){
        return movieRepository.findMovieByImdbId(imdbId);
    }

    public String getFormattedMovieTitle(String imdbId){
        Movie movieByImdbId = movieRepository.findMovieByImdbId(imdbId);
        return movieByImdbId.getTitle().toLowerCase().replace(' ', '-');
    }
}
