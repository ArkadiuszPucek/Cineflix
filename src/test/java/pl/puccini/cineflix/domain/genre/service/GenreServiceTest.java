package pl.puccini.cineflix.domain.genre.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.genre.repository.GenreRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    void setUp() {
        genre1 = new Genre();
        genre1.setGenreType("Comedy");
        genre2 = new Genre();
        genre2.setGenreType("Drama");
    }

    @Test
    void getAllGenresTest() {
        when(genreRepository.findAll()).thenReturn(Arrays.asList(genre1, genre2));

        List<Genre> result = genreService.getAllGenres();

        assertEquals(2, result.size());
        verify(genreRepository).findAll();
    }

    @Test
    void getGenreByTypeTest() {
        when(genreRepository.findByGenreType("Comedy")).thenReturn(genre1);

        Genre result = genreService.getGenreByType("Comedy");

        assertNotNull(result);
        assertEquals("Comedy", result.getGenreType());
        verify(genreRepository).findByGenreType("Comedy");
    }

    @Test
    void getGenresWithMinimumSeriesTest() {
        when(genreRepository.findGenresWithMinimumSeries(10)).thenReturn(Collections.singletonList(genre2));

        List<Genre> result = genreService.getGenresWithMinimumSeries(10);

        assertEquals(1, result.size());
        assertTrue(result.contains(genre2));
        verify(genreRepository).findGenresWithMinimumSeries(10);
    }

    @Test
    void getGenresWithMinimumMoviesTest() {
        when(genreRepository.findGenresWithMinimumMovie(5)).thenReturn(Arrays.asList(genre1, genre2));

        List<Genre> result = genreService.getGenresWithMinimumMovies(5);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(genre1, genre2)));
        verify(genreRepository).findGenresWithMinimumMovie(5);
    }
}
