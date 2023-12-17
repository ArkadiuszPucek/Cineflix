package pl.puccini.cineflix.config.promoBox;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.config.promoBox.moviePromoBox.service.MoviePromotionService;
import pl.puccini.cineflix.config.promoBox.seriesPromoBox.service.SeriesPromotionService;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;

import java.util.List;

@Service
public class PromotionItemFacade {
    private final SeriesPromotionService seriesPromotionService;
    private final MoviePromotionService moviePromotionService;

    public PromotionItemFacade(SeriesPromotionService seriesPromotionService, MoviePromotionService moviePromotionService) {
        this.seriesPromotionService = seriesPromotionService;
        this.moviePromotionService = moviePromotionService;
    }

    public List<SeriesDto> findAllPromotedSeries() {
        return seriesPromotionService.findAllPromotedSeries();
    }

    public List<SeriesDto> getSeriesPromoBox(Long userId) {
        return seriesPromotionService.getSeriesPromoBox(userId);
    }

    public String getSeriesPromoBoxTitle() {
        return seriesPromotionService.getSeriesPromoBoxTitle();
    }

    public void updateSeriesPromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        seriesPromotionService.updateSeriesPromoBox(title, imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
    }

    public List<MovieDto> findAllPromotedMovies() {
        return moviePromotionService.findAllPromotedMovies();
    }

    public List<MovieDto> getMoviePromoBox(Long userId) {
        return moviePromotionService.getMoviePromoBox(userId);
    }

    public String getMoviesPromoBoxTitle() {
        return moviePromotionService.getMoviesPromoBoxTitle();
    }

    public void updateMoviePromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        moviePromotionService.updateMoviePromoBox(title, imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
    }
}
