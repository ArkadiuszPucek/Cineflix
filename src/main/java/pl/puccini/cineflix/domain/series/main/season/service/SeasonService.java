package pl.puccini.cineflix.domain.series.main.season.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.series.main.season.model.Season;
import pl.puccini.cineflix.domain.series.main.season.repository.SeasonRepository;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.series.model.Series;

import java.util.List;
import java.util.Optional;

@Service
public class SeasonService {
    private final SeasonRepository seasonRepository;
    private final SeriesFacade seriesFacade;

    public SeasonService(SeasonRepository seasonRepository, @Lazy SeriesFacade seriesFacade) {
        this.seasonRepository = seasonRepository;
        this.seriesFacade = seriesFacade;
    }

    public Season findOrCreateSeason(String seriesId, int seasonNumber) {
        Series series = seriesFacade.getSeriesByImdbId(seriesId);
        return findSeason(series, seasonNumber).orElseGet(() -> createSeason(series, seasonNumber));
    }

    private Optional<Season> findSeason(Series series, int seasonNumber) {
        return Optional.ofNullable(seasonRepository.findBySeriesAndSeasonNumber(series, seasonNumber));
    }

    private Season createSeason(Series series, int seasonNumber) {
        Season season = new Season();
        season.setSeries(series);
        season.setSeasonNumber(seasonNumber);
        return seasonRepository.save(season);
    }

    public List<Season> getSeasonsForSeries(String seriesImdbId) {
        return seasonRepository.findSeasonsBySeriesImdbId(seriesImdbId);
    }
}
