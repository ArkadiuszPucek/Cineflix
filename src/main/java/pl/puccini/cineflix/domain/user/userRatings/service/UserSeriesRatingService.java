package pl.puccini.cineflix.domain.user.userRatings.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userRatings.model.UserRating;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.userRatings.repository.UserRatingRepository;

import java.util.Optional;

@Service
public class UserSeriesRatingService {
    private final UserRatingRepository userRatingRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;

    public UserSeriesRatingService(UserRatingRepository userRatingRepository, UserRepository userRepository, SeriesRepository seriesRepository) {
        this.userRatingRepository = userRatingRepository;
        this.userRepository = userRepository;
        this.seriesRepository = seriesRepository;
    }

    @Transactional
    public void rateSeries(String imdbId, Long userId, boolean isUpvote) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Series series = seriesRepository.findSeriesByImdbId(imdbId)
                .orElseThrow(() -> new SeriesNotFoundException("Series not found with IMDb ID: " + imdbId));

        processRating(user, series, isUpvote);
    }

    private void processRating(User user, Series series, boolean isUpvote) {
        Optional<UserRating> existingRatingOpt = userRatingRepository.findByUserAndSeries(user, series);

        existingRatingOpt.ifPresentOrElse(existingRating -> {
            if (existingRating.isUpvote() == isUpvote) {
                userRatingRepository.delete(existingRating);
            } else {
                existingRating.setUpvote(isUpvote);
                userRatingRepository.save(existingRating);
            }
        }, () -> {
            UserRating newRating = new UserRating();
            newRating.setUser(user);
            newRating.setSeries(series);
            newRating.setUpvote(isUpvote);
            userRatingRepository.save(newRating);
        });
    }

    public Optional<Boolean> getCurrentUserRatingForSeries(String imdbId, Long userId) {
        return userRatingRepository.findBySeriesImdbIdAndUserId(imdbId, userId)
                .map(UserRating::isUpvote);
    }
    public int getRateUpCountForSeries(Series series) {
        return userRatingRepository.countBySeriesImdbIdAndUpvote(series.getImdbId(), true);
    }

    public int getRateDownCountForSeries(Series series) {
        return userRatingRepository.countBySeriesImdbIdAndUpvote(series.getImdbId(), false);
    }

}
