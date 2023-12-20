package pl.puccini.cineflix.domain.movie.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.IncorrectTypeException;
import pl.puccini.cineflix.domain.exceptions.MovieAlreadyExistsException;
import pl.puccini.cineflix.domain.exceptions.MovieNotFoundException;
import pl.puccini.cineflix.domain.genre.GenreFacade;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.movie.MovieFactory;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.dto.MovieDtoMapper;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.user.userLists.UserListFacade;
import pl.puccini.cineflix.domain.user.userRatings.UserRatingFacade;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final IMDbApiService imdbApiService;
    private final GenreFacade genreFacade;
    private final MovieFactory movieFactory;
    private final UserListFacade userListFacade;
    private final UserRatingFacade userRatingFacade;

    public MovieService(MovieRepository movieRepository, IMDbApiService imdbApiService, GenreFacade genreFacade, MovieFactory movieFactory, UserListFacade userListFacade, UserRatingFacade userRatingFacade) {
        this.movieRepository = movieRepository;
        this.imdbApiService = imdbApiService;
        this.genreFacade = genreFacade;
        this.movieFactory = movieFactory;
        this.userListFacade = userListFacade;
        this.userRatingFacade = userRatingFacade;
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
        Movie existingMovie = movieRepository.findMovieByImdbId(movieDto.getImdbId()).orElseThrow(()->new MovieNotFoundException("Movie not found"));
        if (existingMovie != null) {
            movieFactory.updateMovieWithDto(existingMovie, movieDto);
            movieRepository.save(existingMovie);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteMovieByImdbId(String imdbId) {
        Movie movieByImdbId = movieRepository.findMovieByImdbId(imdbId).orElseThrow(()->new MovieNotFoundException("Movie not found"));
        if (movieByImdbId != null) {
            movieRepository.delete(movieByImdbId);
            return true;
        }
        return false;
    }

    public List<MovieDto> getMovieByGenre(String genre, Long userId){
        Genre genreByType = genreFacade.getGenreByType(genre);
        List<MovieDto> moviesDtos = movieRepository.findAllByGenre(genreByType).stream()
                .map(MovieDtoMapper::map)
                .toList();
        moviesDtos.forEach(movie -> {
            movie.setOnUserList(userListFacade.isOnList(userId, movie.getImdbId()));
            movie.setUserRating(userRatingFacade.getCurrentUserRatingForMovie(movie.getImdbId(), userId).orElse(null));
        });
        return moviesDtos;
    }

    public MovieDto findMovieByTitle(String title, Long userId) {
        Movie movie = movieRepository.findByTitleIgnoreCase(title);
        if (movie == null){
            return null;
        }
        MovieDto mappedMovie = MovieDtoMapper.map(movie);
        mappedMovie.setOnUserList(userListFacade.isOnList(userId, mappedMovie.getImdbId()));
        mappedMovie.setUserRating(userRatingFacade.getCurrentUserRatingForMovie(mappedMovie.getImdbId(), userId).orElse(null));
        return mappedMovie;
    }

    public List<MovieDto> findAllMoviesInService(Long userId){
        List<MovieDto> allMoviesDto = movieRepository.findAll().stream()
                .map(movie -> {
                    MovieDto movieDto = MovieDtoMapper.map(movie);
                    int rateUpCount = userRatingFacade.getRateUpCountForMovies(movie);
                    int rateDownCount = userRatingFacade.getRateDownCountForMovies(movie);
                    movieDto.setRateUpCount(rateUpCount);
                    movieDto.setRateDownCount(rateDownCount);
                    return movieDto;
                })
                .toList();

        allMoviesDto.forEach(movie -> {
            movie.setOnUserList(userListFacade.isOnList(userId, movie.getImdbId()));
            movie.setUserRating(userRatingFacade.getCurrentUserRatingForMovie(movie.getImdbId(), userId).orElse(null));
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
        Movie movieByImdbId = movieRepository.findMovieByImdbId(imdbId).orElseThrow(()->new MovieNotFoundException("Movie not found"));
        return MovieDtoMapper.map(movieByImdbId);
    }

    public Movie getMovieByImdbId(String imdbId){
        return movieRepository.findMovieByImdbId(imdbId).orElseThrow(()->new MovieNotFoundException("Movie not found"));
    }

    public String getNormalizedMovieTitle(String title){
        return title.toLowerCase().replace(' ', '-');
    }

    public int getNumberOfMoviesByGenre(Genre genreType) {
        return movieRepository.countMoviesByGenre(genreType);
    }
}
