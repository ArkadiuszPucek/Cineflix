package pl.puccini.viaplay.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.viaplay.domain.series.model.Season;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.repository.EpisodeRepository;
import pl.puccini.viaplay.domain.series.model.Episode;
import pl.puccini.viaplay.domain.series.repository.SeasonRepository;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final SeasonRepository seasonRepository;
    private final SeriesRepository seriesRepository;

    public EpisodeService(EpisodeRepository episodeRepository, SeasonRepository seasonRepository, SeriesRepository seriesRepository) {
        this.episodeRepository = episodeRepository;
        this.seasonRepository = seasonRepository;
        this.seriesRepository = seriesRepository;
    }

    List<Episode> getAllEpisodes(){
        return episodeRepository.findAllBy();
    }

    public List<EpisodeDto> getEpisodesForSeason(Long seasonId) {
        // Przyjmuję, że EpisodeRepository ma metodę do pobierania epizodów dla danego sezonu
        List<Episode> episodes = episodeRepository.findBySeasonId(seasonId);

        // Następnie możesz przekształcić encje Episode na obiekty EpisodeDto za pomocą mappera
        List<EpisodeDto> episodeDtos = episodes.stream()
                .map(EpisodeDtoMapper::map) // Wykorzystaj swojego mappera
                .collect(Collectors.toList());

        return episodeDtos;
    }

    public Episode addEpisode(EpisodeDto episodeDto, String seriesId, int seasonNumber) {
        // Znajdź lub utwórz sezon
        Season season = findOrCreateSeason(seriesId, seasonNumber);

        // Utwórz nowy epizod z danych DTO
        Episode episode = new Episode();
        episode.setEpisodeNumber(episodeDto.getEpisodeNumber());
        episode.setEpisodeTitle(episodeDto.getEpisodeTitle());
        episode.setMediaUrl(episodeDto.getMediaUrl());
        episode.setDurationMinutes(episodeDto.getDurationMinutes());
        episode.setEpisodeDescription(episodeDto.getEpisodeDescription());
        episode.setSeason(season);

        // Zapisz epizod w bazie danych
        return episodeRepository.save(episode);
    }

    private Season findOrCreateSeason(String seriesId, int seasonNumber) {
        Series series = seriesRepository.findByImdbId(seriesId);
        Season season = seasonRepository.findBySeriesAndSeasonNumber(series, seasonNumber);

        if (season == null) {
            season = new Season();
            season.setSeries(series);
            season.setSeasonNumber(seasonNumber);
            season = seasonRepository.save(season);

            // Dodaj sezon do listy sezonów w serialu i zapisz zmiany
            series.getSeasons().add(season);
            seriesRepository.save(series); // Zapisanie zmian w serialu
        }

        return season;
    }

//    public void addEpisode(EpisodeDto episodeDto, Long seasonId) throws Exception {
//        // Pobranie sezonu na podstawie seasonId
//        Season season = seasonRepository.findById(seasonId)
//                .orElseThrow(() -> new Exception("Sezon o podanym ID nie został znaleziony"));
//
//        // Tworzenie nowego epizodu
//        Episode episode = new Episode();
//        episode.setEpisodeNumber(episodeDto.getEpisodeNumber());
//        episode.setEpisodeTitle(episodeDto.getEpisodeTitle());
//        episode.setMediaUrl(episodeDto.getMediaUrl());
//        episode.setDurationMinutes(episodeDto.getDurationMinutes());
//        episode.setEpisodeDescription(episodeDto.getEpisodeDescription());
//
//        // Ustawienie relacji z sezonem
//        episode.setSeason(season);
//
//        // Zapisanie epizodu w bazie danych
//        episodeRepository.save(episode);
//    }
}
