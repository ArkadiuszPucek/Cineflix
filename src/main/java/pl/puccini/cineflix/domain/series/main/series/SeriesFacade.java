package pl.puccini.cineflix.domain.series.main.series;

import org.springframework.stereotype.Component;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.season.seasonDto.SeasonDto;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.main.series.service.SeriesService;

import java.io.IOException;
import java.util.List;

@Component
public class SeriesFacade {
    private final SeriesService seriesService;

    public SeriesFacade(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    public Series addSeriesByApiIfNotExist(SeriesDto seriesDto) throws IOException, InterruptedException {
        return seriesService.addSeriesByApiIfNotExist(seriesDto);
    }

    public List<SeriesDto> getSeriesByGenre(String genre, Long userId) {
        return seriesService.getSeriesByGenre(genre, userId);
    }

    public SeriesDto findSeriesByTitle(String title, Long userId) {
        return seriesService.findSeriesByTitle(title, userId);
    }

    public SeriesDto getSeriesDtoByImdbId(String imdbId) {
        return seriesService.findSeriesByImdbId(imdbId);
    }

    public Series getSeriesByImdbId(String imdbId) {
        return seriesService.findSeriesByImdbIdSeriesType(imdbId);
    }

    public List<SeriesDto> findAllSeries(Long userId){
        return seriesService.findAllSeriesInService(userId);
    }

    public String formatSeriesTitle(String imdbId){
        return seriesService.getNormalizedSeriesTitle(imdbId);
    }

    public List<SeriesDto> searchSeries(String query) {
        return seriesService.searchSeries(query);
    }

    public void updateSeries(SeriesDto seriesDto) {
        seriesService.updateSeries(seriesDto);
    }

    public boolean deleteSeries(String imdbId) {
        return seriesService.deleteSeriesByImdbId(imdbId);
    }

    public boolean doesSeriesExists(String imdbId) {
        return seriesService.existsByImdbId(imdbId);
    }

    public Series addSeriesManual(SeriesDto seriesDto) throws IOException, InterruptedException {
        return seriesService.addSeriesManualIfNotExist(seriesDto);
    }

    public List<SeasonDto> getSeasonsForSeries(String imdbId){
        return seriesService.getSeasonsForSeries(imdbId);
    }

    public List<EpisodeDto> getEpisodesForSeason(Long seasondId, Long userId){
        return seriesService.getEpisodesForSeason(seasondId, userId);
    }
}
