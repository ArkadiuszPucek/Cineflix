package pl.puccini.cineflix.web.user;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.service.UserListService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HomeService {
    private final MovieService movieService;
    private final UserListService userListService;
    private final SeriesService seriesService;
    private final EpisodeService episodeService;

    public HomeService(MovieService movieService, UserListService userListService, SeriesService seriesService, EpisodeService episodeService) {
        this.movieService = movieService;
        this.userListService = userListService;
        this.seriesService = seriesService;
        this.episodeService = episodeService;
    }

    public Object getRandomPromotedItem(Long userId) {
        List<MovieDto> allPromotedMovies = movieService.findAllPromotedMovies();
        allPromotedMovies.forEach(movie -> movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId())));

        List<SeriesDto> allPromotedSeries = seriesService.findAllPromotedSeries();
        allPromotedSeries.forEach(series -> {
            series.setOnUserList(userListService.isOnList(userId, series.getImdbId()));
            EpisodeDto firstUnwatchedEpisode = episodeService.findFirstUnwatchedEpisode(series.getImdbId(), userId);
            series.setFirstUnwatchedEpisodeId(firstUnwatchedEpisode != null ? firstUnwatchedEpisode.getId() : null);
        });


        List<Object> promotedItems = new ArrayList<>();
        promotedItems.addAll(allPromotedMovies);
        promotedItems.addAll(allPromotedSeries);

        int size = promotedItems.size();
        if (size > 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(size);
            return promotedItems.get(randomIndex);
        } else {
            return null;
        }
    }

    public List<SeriesDto> getSeriesPromoBox(Long userId) {
        return Stream.of("tt7817340", "tt6470478", "tt4655480", "tt6236572", "tt6664638")
                .flatMap(imdbId -> seriesService.getSeriesByImdbId(imdbId).stream())
                .peek(serie -> {
                    serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId()));
                    serie.setUserRating(seriesService.getCurrentUserRatingForSeries(serie.getImdbId(), userId).orElse(null));
                })
                .collect(Collectors.toList());
    }

    public List<MovieDto> getMoviePromoBox(Long userId) {
        return Stream.of("tt0993842", "tt4034228", "tt2304933", "tt6644200", "tt6146586")
                .flatMap(imdbId -> movieService.getMoviesByImdbId(imdbId).stream())
                .peek(movie -> {
                    movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId()));
                    movie.setUserRating(movieService.getCurrentUserRatingForMovie(movie.getImdbId(), userId).orElse(null));
                })
                .collect(Collectors.toList());
    }

}
