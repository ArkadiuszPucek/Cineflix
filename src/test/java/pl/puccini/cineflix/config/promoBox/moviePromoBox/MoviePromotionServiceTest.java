package pl.puccini.cineflix.config.promoBox.moviePromoBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.config.promoBox.moviePromoBox.model.MoviesPromoBox;
import pl.puccini.cineflix.config.promoBox.moviePromoBox.repository.MoviesPromoBoxRepository;
import pl.puccini.cineflix.config.promoBox.moviePromoBox.service.MoviePromotionService;
import pl.puccini.cineflix.domain.genre.GenreFacade;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
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
public class MoviePromotionServiceTest {

    @Mock
    private MoviesPromoBoxRepository moviesPromoBoxRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieFacade movieFacade;
    @Mock
    private GenreFacade genreFacade;

    @Mock
    private UserListFacade userListFacade;

    @Mock
    private UserRatingFacade userRatingFacade;

    @InjectMocks
    private MoviePromotionService moviePromotionService;

    private MoviesPromoBox config;

    @BeforeEach
    void setUp() {
        config = new MoviesPromoBox();
    }

    @Test
    void findAllPromotedMoviesTest() {
        Movie movie1 = testMovie();
        Movie movie2 = testMovie();

        List<Movie> promotedMovies = Arrays.asList(movie1, movie2);
        when(movieRepository.findAllByPromotedIsTrue()).thenReturn(promotedMovies);

        List<MovieDto> result = moviePromotionService.findAllPromotedMovies();

        assertEquals(promotedMovies.size(), result.size());
    }

    @Test
    void getMoviePromoBoxTest() {
        config.setImdbIds("imdb1,imdb2");
        when(moviesPromoBoxRepository.findTopByOrderByIdDesc()).thenReturn(config);
        when(movieRepository.findMovieByImdbId("imdb1")).thenReturn(Optional.of(testMovie()));
        when(movieRepository.findMovieByImdbId("imdb2")).thenReturn(Optional.of(testMovie()));

        List<MovieDto> result = moviePromotionService.getMoviePromoBox(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getMoviesPromoBoxTitleTest() {
        config.setMoviesPromoBoxTitle("Featured Movies");
        when(moviesPromoBoxRepository.findTopByOrderByIdDesc()).thenReturn(config);

        String title = moviePromotionService.getMoviesPromoBoxTitle();

        assertEquals("Featured Movies", title);
    }

    @Test
    void updateMoviePromoBoxTest() {
        when(movieFacade.doesMovieExists(anyString())).thenReturn(true);
        ArgumentCaptor<MoviesPromoBox> captor = ArgumentCaptor.forClass(MoviesPromoBox.class);

        moviePromotionService.updateMoviePromoBox("New Promo Box", "imdb1", "imdb2", "imdb3", "imdb4", "imdb5");

        verify(moviesPromoBoxRepository).save(any(MoviesPromoBox.class));
    }

    private Movie testMovie() {
        Movie movie1 = new Movie();
        Genre genre = new Genre();
        genre.setGenreType("Comedy");
        movie1.setImdbId("tt1234567");
        movie1.setTitle("Movie One");
        movie1.setReleaseYear(2020);
        movie1.setImageUrl("https://example.com/image1.jpg");
        movie1.setBackgroundImageUrl("https://example.com/bgimage1.jpg");
        movie1.setMediaUrl("https://www.youtube.com/watch?v=example1");
        movie1.setTimeline(120);
        movie1.setAgeLimit(16);
        movie1.setDescription("Description of Movie One");
        movie1.setStaff("Actor 1, Actor 2");
        movie1.setDirectedBy("Director 1");
        movie1.setLanguages("English");
        movie1.setPromoted(true);
        movie1.setImdbRating(7.5);
        movie1.setImdbUrl("https://www.imdb.com/title/tt1234567/");
        movie1.setGenre(genre);
        movie1.setRatings(Set.of());
        return movie1;
    }
}

