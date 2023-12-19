package pl.puccini.cineflix.config.carousel.series.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.config.carousel.series.model.SeriesCarouselConfig;
import pl.puccini.cineflix.config.carousel.series.repository.SeriesCarouselConfigRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeriesCarouselServiceTest {

    @Mock
    private SeriesCarouselConfigRepository seriesCarouselConfigRepository;

    @InjectMocks
    private SeriesCarouselService seriesCarouselService;

    private SeriesCarouselConfig config;

    @BeforeEach
    void setUp() {
        config = new SeriesCarouselConfig();
        config.setId(1L);
        config.setActiveGenres("Action,Drama");
    }

    @Test
    void saveSelectedGenresTest() {
        when(seriesCarouselConfigRepository.findById(1L)).thenReturn(Optional.of(config));
        List<String> genres = Arrays.asList("Drama", "Thriller");

        seriesCarouselService.saveSelectedGenres(genres);

        verify(seriesCarouselConfigRepository).save(any(SeriesCarouselConfig.class));
    }

    @Test
    void getSelectedGenresTest() {
        when(seriesCarouselConfigRepository.findTopByOrderByIdDesc()).thenReturn(config);

        List<String> selectedGenres = seriesCarouselService.getSelectedGenres();

        assertEquals(Arrays.asList("Action", "Drama"), selectedGenres);
    }

    @Test
    void getConfigByIdTest() {
        when(seriesCarouselConfigRepository.findById(1L)).thenReturn(Optional.of(config));

        SeriesCarouselConfig fetchedConfig = seriesCarouselService.getConfigById(1L);

        assertNotNull(fetchedConfig);
        assertEquals(1L, fetchedConfig.getId());
    }

    @Test
    void getTopByOrderByIdDescTest() {
        when(seriesCarouselConfigRepository.findTopByOrderByIdDesc()).thenReturn(config);

        SeriesCarouselConfig fetchedConfig = seriesCarouselService.getTopByOrderByIdDesc();

        assertNotNull(fetchedConfig);
        assertEquals("Action,Drama", fetchedConfig.getActiveGenres());
    }
}
