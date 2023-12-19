package pl.puccini.cineflix.domain.kids;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KidsSeriesServiceTest {
    @Mock
    private SeriesFacade seriesFacade;
    @InjectMocks
    private KidsSeriesService kidsSeriesService;

    @Test
    void getAlKidsSeriesTest(){
        Long userId = 1L;
        String kidsGenre = "Kids";

        List<SeriesDto> expectedSeries = Arrays.asList(new SeriesDto(), new SeriesDto());

        when(seriesFacade.getSeriesByGenre(kidsGenre, userId)).thenReturn(expectedSeries);

        List<SeriesDto> result = kidsSeriesService.getAllKidsSeries(userId);
        assertEquals(expectedSeries, result);
        verify(seriesFacade).getSeriesByGenre(kidsGenre,userId);
    }


}
