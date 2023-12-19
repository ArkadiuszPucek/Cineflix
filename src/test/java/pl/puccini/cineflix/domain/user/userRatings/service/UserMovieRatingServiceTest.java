package pl.puccini.cineflix.domain.user.userRatings.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.userRatings.model.UserRating;
import pl.puccini.cineflix.domain.user.userRatings.repository.UserRatingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserMovieRatingServiceTest {

    @Mock
    private UserRatingRepository userRatingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MovieRepository movieRepository;
    @InjectMocks
    private UserMovieRatingService userMovieRatingService;

    private final Long userId = 1L;
    private final String imdbId = "tt1234567";
    private User user;
    private Movie movie;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        movie = new Movie();
        movie.setImdbId(imdbId);
    }

    @Test
    void whenRatingMovie_thenSavesRating() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findMovieByImdbId(imdbId)).thenReturn(Optional.of(movie));
        when(userRatingRepository.findByUserAndMovie(user, movie)).thenReturn(Optional.empty());

        userMovieRatingService.rateMovie(imdbId, userId, true);

        verify(userRatingRepository).save(any(UserRating.class));
    }

    @Test
    void whenUpdatingRating_thenUpdatesExistingRating() {
        UserRating existingRating = new UserRating();
        existingRating.setUpvote(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findMovieByImdbId(imdbId)).thenReturn(Optional.of(movie));
        when(userRatingRepository.findByUserAndMovie(user, movie)).thenReturn(Optional.of(existingRating));

        userMovieRatingService.rateMovie(imdbId, userId, true);

        verify(userRatingRepository).save(existingRating);
        assertTrue(existingRating.isUpvote());
    }

    @Test
    void whenRatingIsSame_thenDeletesRating() {
        UserRating existingRating = new UserRating();
        existingRating.setUpvote(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findMovieByImdbId(imdbId)).thenReturn(Optional.of(movie));
        when(userRatingRepository.findByUserAndMovie(user, movie)).thenReturn(Optional.of(existingRating));

        userMovieRatingService.rateMovie(imdbId, userId, true);

        verify(userRatingRepository).delete(existingRating);
    }

    @Test
    void whenGettingRateUpCount_thenReturnsCorrectCount() {
        int expectedCount = 5;
        when(userRatingRepository.countByMovieImdbIdAndUpvote(imdbId, true)).thenReturn(expectedCount);

        int count = userMovieRatingService.getRateUpCountForMovies(movie);
        assertEquals(expectedCount, count);
    }

    @Test
    void whenGettingRateDownCount_thenReturnsCorrectCount() {
        int expectedCount = 3;
        when(userRatingRepository.countByMovieImdbIdAndUpvote(imdbId, false)).thenReturn(expectedCount);

        int count = userMovieRatingService.getRateDownCountForMovies(movie);
        assertEquals(expectedCount, count);
    }
}