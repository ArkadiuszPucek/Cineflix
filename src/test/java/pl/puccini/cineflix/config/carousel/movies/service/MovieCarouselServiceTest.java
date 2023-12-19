package pl.puccini.cineflix.config.carousel.movies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.config.carousel.movies.model.MoviesCarouselConfig;
import pl.puccini.cineflix.config.carousel.movies.repository.MovieCarouselConfigRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieCarouselServiceTest {

    @Mock
    private MovieCarouselConfigRepository movieCarouselConfigRepository;

    @InjectMocks
    private MovieCarouselService movieCarouselService;

    private MoviesCarouselConfig config;

    @BeforeEach
    void setUp() {
        config = new MoviesCarouselConfig();
        config.setId(1L);
        config.setActiveGenres("Action,Drama");
    }

    @Test
    void saveSelectedGenresTest() {
        when(movieCarouselConfigRepository.findById(1L)).thenReturn(Optional.of(config));
        List<String> genres = Arrays.asList("Comedy", "Thriller");

        movieCarouselService.saveSelectedGenres(genres);

        verify(movieCarouselConfigRepository).save(any(MoviesCarouselConfig.class));
    }

    @Test
    void getSelectedGenresTest() {
        when(movieCarouselConfigRepository.findTopByOrderByIdDesc()).thenReturn(config);

        List<String> selectedGenres = movieCarouselService.getSelectedGenres();

        assertEquals(Arrays.asList("Action", "Drama"), selectedGenres);
    }

    @Test
    void getConfigByIdTest() {
        when(movieCarouselConfigRepository.findById(1L)).thenReturn(Optional.of(config));

        MoviesCarouselConfig fetchedConfig = movieCarouselService.getConfigById(1L);

        assertNotNull(fetchedConfig);
        assertEquals(1L, fetchedConfig.getId());
    }

    @Test
    void getTopByOrderByIdDescTest() {
        when(movieCarouselConfigRepository.findTopByOrderByIdDesc()).thenReturn(config);

        MoviesCarouselConfig fetchedConfig = movieCarouselService.getTopByOrderByIdDesc();

        assertNotNull(fetchedConfig);
        assertEquals("Action,Drama", fetchedConfig.getActiveGenres());
    }
}
