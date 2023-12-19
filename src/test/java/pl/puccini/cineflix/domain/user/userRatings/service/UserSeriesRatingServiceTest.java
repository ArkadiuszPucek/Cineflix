package pl.puccini.cineflix.domain.user.userRatings.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.userRatings.model.UserRating;
import pl.puccini.cineflix.domain.user.userRatings.repository.UserRatingRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserSeriesRatingServiceTest {

    @Mock
    private UserRatingRepository userRatingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SeriesRepository seriesRepository;
    @InjectMocks
    private UserSeriesRatingService userSeriesRatingService;

    private final Long userId = 1L;
    private final String imdbId = "tt7654321";
    private User user;
    private Series series;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        series = new Series();
        series.setImdbId(imdbId);
    }

    @Test
    void whenRatingSeries_thenSavesRating() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seriesRepository.findSeriesByImdbId(imdbId)).thenReturn(Optional.of(series));
        when(userRatingRepository.findByUserAndSeries(user, series)).thenReturn(Optional.empty());

        userSeriesRatingService.rateSeries(imdbId, userId, true);

        verify(userRatingRepository).save(any(UserRating.class));
    }

    @Test
    void whenUpdatingRating_thenUpdatesExistingRating() {
        UserRating existingRating = new UserRating();
        existingRating.setUpvote(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seriesRepository.findSeriesByImdbId(imdbId)).thenReturn(Optional.of(series));
        when(userRatingRepository.findByUserAndSeries(user, series)).thenReturn(Optional.of(existingRating));

        userSeriesRatingService.rateSeries(imdbId, userId, true);

        verify(userRatingRepository).save(existingRating);
        assertTrue(existingRating.isUpvote());
    }

    @Test
    void whenRatingIsSame_thenDeletesRating() {
        UserRating existingRating = new UserRating();
        existingRating.setUpvote(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seriesRepository.findSeriesByImdbId(imdbId)).thenReturn(Optional.of(series));
        when(userRatingRepository.findByUserAndSeries(user, series)).thenReturn(Optional.of(existingRating));

        userSeriesRatingService.rateSeries(imdbId, userId, true);

        verify(userRatingRepository).delete(existingRating);
    }

    @Test
    void whenGettingRateUpCount_thenReturnsCorrectCount() {
        int expectedCount = 5;
        when(userRatingRepository.countBySeriesImdbIdAndUpvote(imdbId, true)).thenReturn(expectedCount);

        int count = userSeriesRatingService.getRateUpCountForSeries(series);
        assertEquals(expectedCount, count);
    }

    @Test
    void whenGettingRateDownCount_thenReturnsCorrectCount() {
        int expectedCount = 3;
        when(userRatingRepository.countBySeriesImdbIdAndUpvote(imdbId, false)).thenReturn(expectedCount);

        int count = userSeriesRatingService.getRateDownCountForSeries(series);
        assertEquals(expectedCount, count);
    }
}