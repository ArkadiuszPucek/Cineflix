package pl.puccini.cineflix.config.promoBox.seriesPromoBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.config.promoBox.seriesPromoBox.model.SeriesPromoBox;
import pl.puccini.cineflix.config.promoBox.seriesPromoBox.repository.SeriesPromoBoxRepository;
import pl.puccini.cineflix.config.promoBox.seriesPromoBox.service.SeriesPromotionService;
import pl.puccini.cineflix.domain.genre.GenreFacade;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.user.userLists.UserListFacade;
import pl.puccini.cineflix.domain.user.userRatings.UserRatingFacade;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeriesPromotionServiceTest {

    @Mock
    private SeriesPromoBoxRepository seriesPromoBoxRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private SeriesFacade seriesFacade;

    @Mock
    private GenreFacade genreFacade;

    @Mock
    private UserListFacade userListFacade;

    @Mock
    private UserRatingFacade userRatingFacade;

    @InjectMocks
    private SeriesPromotionService seriesPromotionService;

    private SeriesPromoBox config;

    @BeforeEach
    void setUp() {
        config = new SeriesPromoBox();
        config.setSeriesPromoBoxTitle("series promo box");
        config.setImdbIds("id1,id2");
        config.setId(1L);
    }

    @Test
    void findAllPromotedSeriesTest() {
        Series series1 = testSeries();
        Series series2 = testSeries();

        List<Series> promotedSeries = Arrays.asList(series1, series2);
        when(seriesRepository.findAllByPromotedIsTrue()).thenReturn(promotedSeries);

        List<SeriesDto> result = seriesPromotionService.findAllPromotedSeries();

        assertEquals(promotedSeries.size(), result.size());
    }

    @Test
    void getSeriesPromoBoxTest() {
        Series series1 = testSeries();
        Series series2 = testSeries();
        series2.setImdbId("id2");
        when(seriesPromoBoxRepository.findTopByOrderByIdDesc()).thenReturn(config);
        when(seriesRepository.findSeriesByImdbId("id1")).thenReturn(Optional.of(series1));
        when(seriesRepository.findSeriesByImdbId("id2")).thenReturn(Optional.of(series2));

        List<SeriesDto> result = seriesPromotionService.getSeriesPromoBox(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getSeriesPromoBoxTitleTest() {
        config.setSeriesPromoBoxTitle("Trending Series");
        when(seriesPromoBoxRepository.findTopByOrderByIdDesc()).thenReturn(config);

        String title = seriesPromotionService.getSeriesPromoBoxTitle();

        assertEquals("Trending Series", title);
    }

    @Test
    void updateSeriesPromoBoxTest() {
        when(seriesFacade.doesSeriesExists(anyString())).thenReturn(true);
        ArgumentCaptor<SeriesPromoBox> captor = ArgumentCaptor.forClass(SeriesPromoBox.class);

        seriesPromotionService.updateSeriesPromoBox("New Promo Box", "imdb1", "imdb2", "imdb3", "imdb4", "imdb5");

        verify(seriesPromoBoxRepository).save(any(SeriesPromoBox.class));
    }

    private Series testSeries() {
        Series series = new Series();
        Genre genre = new Genre();
        genre.setGenreType("Comedy");
        series.setImdbId("id1");
        series.setTitle("Movie One");
        series.setReleaseYear(2020);
        series.setImageUrl("https://example.com/image1.jpg");
        series.setBackgroundImageUrl("https://example.com/bgimage1.jpg");
        series.setDescription("Description of Series");
        series.setStaff("Actor 1, Actor 2");
        series.setPromoted(true);
        series.setAgeLimit(16);
        series.setImdbRating(7.5);
        series.setImdbUrl("https://www.imdb.com/title/tt1234567/");
        series.setSeasonsCount(2);
        series.setGenre(genre);
        series.setRatings(Set.of());
        return series;
    }
}
