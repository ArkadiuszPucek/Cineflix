package pl.puccini.cineflix.domain.movie;

import org.springframework.stereotype.Component;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.user.userLists.service.UserListService;

import java.io.IOException;
import java.util.List;

@Component
public class MovieFacade {
    private final MovieService movieService;


    public MovieFacade(MovieService movieService) {
        this.movieService = movieService;
    }

    public Movie addMovieIfNotExist(MovieDto movieDto) throws IOException, InterruptedException {
        return movieService.addMovieByApiIfNotExist(movieDto);
    }

    public List<MovieDto> getMovieByGenre(String genre, Long userId) {
        return movieService.getMovieByGenre(genre, userId);
    }

    public MovieDto findMovieByTitle(String title, Long userId) {
        return movieService.findMovieByTitle(title, userId);
    }

    public MovieDto getMovieDtoByImdbId(String imdbId) {
        return movieService.findMovieByImdbId(imdbId);
    }

    public List<MovieDto> findAllMovies(Long userId){
        return movieService.findAllMoviesInService(userId);
    }

    public List<MovieDto> searchMovies(String query) {
        return movieService.searchMovies(query);
    }

    public boolean updateMovie(MovieDto movieDto) {
        return movieService.updateMovie(movieDto);
    }

    public boolean deleteMovie(String imdbId) {
        return movieService.deleteMovieByImdbId(imdbId);
    }
    public boolean doesMovieExists(String imdbId) {
        return movieService.existsByImdbId(imdbId);
    }
    public void addMovieManual(MovieDto movieDto){
        movieService.addMovieManual(movieDto);
    }

    public String getNormalizedMovieTitle(String title) {
        return movieService.getNormalizedMovieTitle(title);
    }
    public int getNumberOfMoviesByGenre(Genre genreType) {
        return movieService.getNumberOfMoviesByGenre(genreType);
    }
}
