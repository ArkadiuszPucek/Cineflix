package pl.puccini.cineflix.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.series.model.Season;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.repository.SeasonRepository;
import pl.puccini.cineflix.domain.series.repository.SeriesRepository;

import java.util.List;

@Service
public class SeasonService {
    private final SeasonRepository seasonRepository;
    private final SeriesRepository seriesRepository;

    public SeasonService(SeasonRepository seasonRepository, SeriesRepository seriesRepository) {
        this.seasonRepository = seasonRepository;
        this.seriesRepository = seriesRepository;
    }

    public List<Season> getAllSeasons(){
        return seasonRepository.findAllBy();
    }

    public Season findOrCreateSeason(String seriesId, int seasonNumber) {
        Series series = seriesRepository.findByImdbId(seriesId);
        Season season = seasonRepository.findBySeriesAndSeasonNumber(series, seasonNumber);

        if (season == null) {
            season = new Season();
            season.setSeries(series);
            season.setSeasonNumber(seasonNumber);
            season = seasonRepository.save(season);

            series.getSeasons().add(season);
            seriesRepository.save(series);
        }

        return season;
    }



}
