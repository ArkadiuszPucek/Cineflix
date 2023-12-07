package pl.puccini.cineflix.web.user;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesCarouselConfigDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.service.UserListService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class HomeService {
    private final MovieService movieService;
    private final UserListService userListService;
    private final SeriesService seriesService;
    private final EpisodeService episodeService;
    private final GenreService genreService;

    public HomeService(MovieService movieService, UserListService userListService, SeriesService seriesService, EpisodeService episodeService, GenreService genreService) {
        this.movieService = movieService;
        this.userListService = userListService;
        this.seriesService = seriesService;
        this.episodeService = episodeService;
        this.genreService = genreService;
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

    public List<SeriesCarouselConfigDto> getSeriesCarouselsByActiveGenres(Long userId) {
        List<String> activeGenres = genreService.getSelectedGenres();

        List<SeriesCarouselConfigDto> carousels = new ArrayList<>();

        for (String genre : activeGenres) {
            SeriesCarouselConfigDto config = new SeriesCarouselConfigDto();
            config.setGenre(genre);
            config.setSeries(seriesService.getSeriesByGenre(genre, userId));
            config.setActive(true);
            carousels.add(config);
        }
        return carousels;
    }

}
