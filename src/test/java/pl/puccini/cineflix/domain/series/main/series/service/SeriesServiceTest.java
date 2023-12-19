package pl.puccini.cineflix.domain.series.main.series.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.series.main.series.SeriesFactory;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeriesServiceTest {

    @Mock
    private SeriesRepository seriesRepository;
    @Mock
    private IMDbApiService imdbApiService;
    @Mock
    private SeriesFactory seriesFactory;
    @InjectMocks
    private SeriesService seriesService;

    private Series testSeries;
    private SeriesDto testSeriesDto;

    @BeforeEach
    void setUp() {
        testSeries = new Series();
        testSeries.setImdbId("testImdbId");
        testSeriesDto = new SeriesDto();
        testSeriesDto.setImdbId("testImdbId");
    }

    @Test
    void addSeriesByApiIfNotExist_NotExisting_AddsSeries() throws IOException, InterruptedException {
        when(seriesRepository.existsByImdbId("testImdbId")).thenReturn(false);
        when(imdbApiService.fetchIMDbForTypeCheck("testImdbId")).thenReturn("show");
        when(imdbApiService.fetchIMDbDataForSeries("testImdbId")).thenReturn(testSeriesDto);
        when(seriesFactory.createSeries(testSeriesDto, false)).thenReturn(testSeries);

        Series result = seriesService.addSeriesByApiIfNotExist(testSeriesDto);

        assertNotNull(result);
        verify(seriesRepository).save(result);
    }

    @Test
    void addSeriesByApiIfNotExist_Existing_ThrowsException() {
        when(seriesRepository.existsByImdbId("testImdbId")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> seriesService.addSeriesByApiIfNotExist(testSeriesDto));
    }

    @Test
    void updateSeries_ExistingSeries_UpdatesSeries() {
        when(seriesRepository.findSeriesByImdbId("testImdbId")).thenReturn(Optional.of(testSeries));

        seriesService.updateSeries(testSeriesDto);

        verify(seriesFactory).updateSeriesWithDto(testSeries, testSeriesDto);
        verify(seriesRepository).save(testSeries);
    }

    @Test
    void deleteSeriesByImdbId_ExistingSeries_DeletesSeries() {
        when(seriesRepository.findSeriesByImdbId("testImdbId")).thenReturn(Optional.of(testSeries));

        boolean result = seriesService.deleteSeriesByImdbId("testImdbId");

        assertTrue(result);
        verify(seriesRepository).delete(testSeries);
    }

    @Test
    void deleteSeriesByImdbId_NonExistingSeries_ReturnsFalse() {
        when(seriesRepository.findSeriesByImdbId("nonExistingImdbId")).thenReturn(Optional.empty());

        boolean result = seriesService.deleteSeriesByImdbId("nonExistingImdbId");

        assertFalse(result);
    }

}
