package pl.puccini.cineflix.web.user;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.config.carousel.movies.service.MovieCarouselService;
import pl.puccini.cineflix.config.carousel.series.service.SeriesCarouselService;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.config.carousel.movies.dto.MoviesCarouselConfigDto;
import pl.puccini.cineflix.domain.movie.service.MoviePromotionService;
import pl.puccini.cineflix.domain.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.config.carousel.series.dto.SeriesCarouselConfigDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.series.service.SeriesPromotionService;
import pl.puccini.cineflix.domain.series.service.SeriesService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class HomeService {
    private final MovieFacade movieFacade;
    private final EpisodeService episodeService;
    private final SeriesCarouselService seriesCarouselService;
    private final MovieCarouselService movieCarouselService;
    private final MoviePromotionService moviePromotionService;
    private final SeriesPromotionService seriesPromotionService;
    private final SeriesFacade seriesFacade;

    public HomeService(MovieFacade movieFacade, EpisodeService episodeService, SeriesCarouselService seriesCarouselService, MovieCarouselService movieCarouselService, MoviePromotionService moviePromotionService, SeriesPromotionService seriesPromotionService, SeriesFacade seriesFacade) {
        this.movieFacade = movieFacade;
        this.episodeService = episodeService;
        this.seriesCarouselService = seriesCarouselService;
        this.movieCarouselService = movieCarouselService;
        this.moviePromotionService = moviePromotionService;
        this.seriesPromotionService = seriesPromotionService;
        this.seriesFacade = seriesFacade;
    }

    public Object getRandomPromotedItem(Long userId) {
        List<MovieDto> allPromotedMovies = moviePromotionService.findAllPromotedMovies();
        allPromotedMovies.forEach(movie -> movie.setOnUserList(movieFacade.isMovieOnUserList(userId, movie.getImdbId())));

        List<SeriesDto> allPromotedSeries = seriesPromotionService.findAllPromotedSeries();
        allPromotedSeries.forEach(series -> {
            series.setOnUserList(movieFacade.isMovieOnUserList(userId, series.getImdbId()));
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
        List<String> activeGenres = seriesCarouselService.getSelectedGenres();
        List<SeriesCarouselConfigDto> carousels = new ArrayList<>();

        for (String genre : activeGenres) {
            if (!genre.isEmpty()) {
                SeriesCarouselConfigDto config = new SeriesCarouselConfigDto();
                config.setGenre(genre);
                config.setSeries(seriesFacade.getSeriesByGenre(genre, userId));
                config.setActive(true);
                carousels.add(config);
            }
        }
        return carousels;
    }

    public List<MoviesCarouselConfigDto> getMoviesCarouselsByActiveGenres(Long userId) {
        List<String> activeGenres = movieCarouselService.getSelectedGenres();

        List<MoviesCarouselConfigDto> carousels = new ArrayList<>();

        for (String genre : activeGenres) {
            if (!genre.isEmpty()) {
                MoviesCarouselConfigDto config = new MoviesCarouselConfigDto();
                config.setGenre(genre);
                config.setMovies(movieFacade.getMovieByGenre(genre, userId));
                config.setActive(true);
                carousels.add(config);
            }
        }
        return carousels;
    }

}
