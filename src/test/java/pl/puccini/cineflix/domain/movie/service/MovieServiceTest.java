package pl.puccini.cineflix.domain.movie.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.exceptions.MovieAlreadyExistsException;
import pl.puccini.cineflix.domain.exceptions.MovieNotFoundException;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.movie.MovieFactory;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private IMDbApiService imdbApiService;
    @Mock
    private MovieFactory movieFactory;

    @InjectMocks
    private MovieService movieService;

    @Test
    void throwExceptionWhenMovieAlreadyExists() {
        MovieDto movieDto = new MovieDto();
        movieDto.setImdbId("testId");
        when(movieRepository.existsByImdbId("testId")).thenReturn(true);

        assertThrows(MovieAlreadyExistsException.class, () -> movieService.addMovieByApiIfNotExist(movieDto));
    }


    @Test
    void deleteExistingMovie() {
        String imdbId = "testId";
        when(movieRepository.findMovieByImdbId(imdbId)).thenReturn(Optional.of(new Movie()));

        boolean result = movieService.deleteMovieByImdbId(imdbId);

        assertTrue(result);
    }

    @Test
    void throwExceptionWhenDeletingNonexistentMovie() {
        String imdbId = "testId";
        when(movieRepository.findMovieByImdbId(imdbId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovieByImdbId(imdbId));
    }

    @Test
    void returnNullWhenMovieTitleNotFound() {
        String title = "Nonexistent Movie";

        when(movieRepository.findByTitleIgnoreCase(title)).thenReturn(null);

        assertNull(movieService.findMovieByTitle(title, 1L));
    }

    @Test
    void updateExistingMovie() {
        MovieDto movieDto = new MovieDto();
        movieDto.setImdbId("testId");
        when(movieRepository.findMovieByImdbId("testId")).thenReturn(Optional.of(new Movie()));

        boolean result = movieService.updateMovie(movieDto);

        assertTrue(result);
    }
}