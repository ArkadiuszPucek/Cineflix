package pl.puccini.cineflix.web.home;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.config.carousel.CarouselFacade;
import pl.puccini.cineflix.config.carousel.movies.dto.MoviesCarouselConfigDto;
import pl.puccini.cineflix.config.carousel.series.dto.SeriesCarouselConfigDto;
import pl.puccini.cineflix.config.promoBox.PromotionItemFacade;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFacade;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.user.userLists.UserListFacade;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HomeServiceTest {

    @Mock
    private MovieFacade movieFacade;

    @Mock
    private EpisodeFacade episodeFacade;

    @Mock
    private CarouselFacade carouselFacade;

    @Mock
    private PromotionItemFacade promotionItemFacade;

    @Mock
    private SeriesFacade seriesFacade;

    @Mock
    private UserListFacade userListFacade;

    @InjectMocks
    private HomeService homeService;

    @Test
    void getRandomPromotedItemTest() {
        when(promotionItemFacade.findAllPromotedMovies()).thenReturn(Collections.emptyList());
        when(promotionItemFacade.findAllPromotedSeries()).thenReturn(Collections.emptyList());

        Object result = homeService.getRandomPromotedItem(1L);

        assertNull(result);
    }

    @Test
    void getSeriesCarouselsByActiveGenresTest() {
        when(carouselFacade.getSelectedGenresForSeries()).thenReturn(Collections.singletonList("Action"));

        List<SeriesCarouselConfigDto> result = homeService.getSeriesCarouselsByActiveGenres(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Action", result.get(0).getGenre());
    }

    @Test
    void getMoviesCarouselsByActiveGenresTest() {
        when(carouselFacade.getSelectedGenresForMovies()).thenReturn(Collections.singletonList("Comedy"));

        List<MoviesCarouselConfigDto> result = homeService.getMoviesCarouselsByActiveGenres(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Comedy", result.get(0).getGenre());
    }

}
