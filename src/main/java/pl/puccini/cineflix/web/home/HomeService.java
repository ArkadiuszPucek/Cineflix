package pl.puccini.cineflix.web.home;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.config.carousel.CarouselFacade;
import pl.puccini.cineflix.config.promoBox.PromotionItemFacade;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.config.carousel.movies.dto.MoviesCarouselConfigDto;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFacade;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.config.carousel.series.dto.SeriesCarouselConfigDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.user.userLists.UserListFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class HomeService {
    private final MovieFacade movieFacade;
    private final EpisodeFacade episodeFacade;
    private final CarouselFacade carouselFacade;
    private final PromotionItemFacade promotionItemFacade;
    private final SeriesFacade seriesFacade;
    private final UserListFacade userListFacade;

    public HomeService(MovieFacade movieFacade, EpisodeFacade episodeFacade, CarouselFacade carouselFacade, PromotionItemFacade promotionItemFacade, SeriesFacade seriesFacade, UserListFacade userListFacade) {
        this.movieFacade = movieFacade;
        this.episodeFacade = episodeFacade;
        this.carouselFacade = carouselFacade;
        this.promotionItemFacade = promotionItemFacade;
        this.seriesFacade = seriesFacade;
        this.userListFacade = userListFacade;
    }

    public Object getRandomPromotedItem(Long userId) {
        List<MovieDto> allPromotedMovies = promotionItemFacade.findAllPromotedMovies();
        allPromotedMovies.forEach(movie -> movie.setOnUserList(userListFacade.isOnList(userId, movie.getImdbId())));

        List<SeriesDto> allPromotedSeries = promotionItemFacade.findAllPromotedSeries();
        allPromotedSeries.forEach(series -> {
            series.setOnUserList(userListFacade.isOnList(userId, series.getImdbId()));
            EpisodeDto firstUnwatchedEpisode = episodeFacade.findFirstUnwatchedEpisode(series.getImdbId(), userId);
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
        List<String> activeGenres = carouselFacade.getSelectedGenresForSeries();
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
        List<String> activeGenres = carouselFacade.getSelectedGenresForMovies();

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
