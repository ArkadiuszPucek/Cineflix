package pl.puccini.cineflix.domain.kids;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KidsMovieServiceTest {
    @Mock
    private MovieFacade movieFacade;
    @InjectMocks
    private KidsMovieService kidsMovieService;

    @Test
    void getAllKidsSeriesTest() {
        Long userId = 1L;
        String kidsGenre = "Kids";

        List<MovieDto> expectedMovies = Arrays.asList(new MovieDto(), new MovieDto());

        when(movieFacade.getMovieByGenre(kidsGenre, userId)).thenReturn(expectedMovies);

        List<MovieDto> result = kidsMovieService.getAllKidsMovies(userId);

        assertEquals(expectedMovies, result);
        verify(movieFacade).getMovieByGenre(kidsGenre, userId);
    }

}
