package pl.puccini.cineflix.web.admin.seriesManagement;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.puccini.cineflix.config.carousel.CarouselFacade;
import pl.puccini.cineflix.config.promoBox.PromotionItemFacade;
import pl.puccini.cineflix.domain.UserUtils;
import pl.puccini.cineflix.domain.genre.GenreFacade;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFacade;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;

import java.io.IOException;
import java.util.List;

@Service
public class SeriesManagementFacade {
    private final SeriesFacade seriesFacade;
    private final UserUtils userUtils;
    private final GenreFacade genreFacade;
    private final EpisodeFacade episodeFacade;
    private final PromotionItemFacade promotionItemFacade;
    private final CarouselFacade carouselFacade;

    public SeriesManagementFacade(SeriesFacade seriesFacade, UserUtils userUtils, GenreFacade genreFacade, EpisodeFacade episodeFacade, PromotionItemFacade promotionItemFacade, CarouselFacade carouselFacade) {
        this.seriesFacade = seriesFacade;
        this.userUtils = userUtils;
        this.genreFacade = genreFacade;
        this.episodeFacade = episodeFacade;
        this.promotionItemFacade = promotionItemFacade;
        this.carouselFacade = carouselFacade;
    }

    public boolean doesSeriesExists(String imdbId) {
        return seriesFacade.doesSeriesExists(imdbId);
    }

    public Series addSeriesManual(SeriesDto seriesDto) throws IOException, InterruptedException {
        return seriesFacade.addSeriesManual(seriesDto);
    }

    public void addAvatarUrlToModel(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
    }

    public Long getUserIdFromAuthentication(Authentication authentication) {
        return userUtils.getUserIdFromAuthentication(authentication);
    }

    public List<Genre> getAllGenres() {
        return genreFacade.getAllGenres();
    }

    public String processEpisodeAddition(EpisodeDto episodeDto, String seriesId, int seasonNumber, int episodeNumber, int seasonsCount, String action, String seriesTitle) {
        return episodeFacade.processEpisodeAddition(episodeDto, seriesId, seasonNumber, episodeNumber, seasonsCount, action, seriesTitle);
    }

    public Series addSeriesByApiIfNotExist(SeriesDto seriesDto) throws IOException, InterruptedException {
        return seriesFacade.addSeriesByApiIfNotExist(seriesDto);
    }

    public String formatSeriesTitle(String imdbId){
        return seriesFacade.formatSeriesTitle(imdbId);
    }

    public List<SeriesDto> findAllSeries(Long userId){
        return seriesFacade.findAllSeries(userId);
    }

    public SeriesDto getSeriesDtoByImdbId(String imdbId) {
        return seriesFacade.getSeriesDtoByImdbId(imdbId);
    }

    public void updateSeries(SeriesDto seriesDto) {
        seriesFacade.updateSeries(seriesDto);
    }

    public boolean deleteSeries(String imdbId) {
        return seriesFacade.deleteSeries(imdbId);
    }

    public Series getSeriesByImdbId(String imdbId) {
        return seriesFacade.getSeriesByImdbId(imdbId);
    }

    public EpisodeDto getEpisodeById(Long episodeId){
        return episodeFacade.getEpisodeById(episodeId);
    }

    public Episode updateEpisode(EpisodeDto episodeDto) {
        return episodeFacade.updateEpisode(episodeDto);
    }

    public Episode deleteEpisodeById(Long episodeId) {
        return episodeFacade.deleteEpisodeById(episodeId);
    }

    public List<SeriesDto> getSeriesPromoBox(Long userId) {
        return promotionItemFacade.getSeriesPromoBox(userId);
    }

    public String getSeriesPromoBoxTitle() {
        return promotionItemFacade.getSeriesPromoBoxTitle();
    }

    public void updateSeriesPromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        promotionItemFacade.updateSeriesPromoBox(title, imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
    }

    public List<Genre> getGenresWithMinimumSeries(int minSeriesCount) {
        return genreFacade.getGenresWithMinimumSeries(minSeriesCount);
    }

    public List<String> getSelectedGenresForSeries() {
        return carouselFacade.getSelectedGenresForSeries();
    }

    public void saveSelectedGenresForSeries(List<String> selectedGenres) {
        carouselFacade.saveSelectedGenresForSeries(selectedGenres);
    }









}
