package pl.puccini.viaplay.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeInfoDto;
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
        List<Episode> episodes = episodeRepository.findBySeasonId(seasonId);

        List<EpisodeDto> episodeDtos = episodes.stream()
                .map(EpisodeDtoMapper::map)
                .collect(Collectors.toList());

        return episodeDtos;
    }

    public Episode addEpisode(EpisodeDto episodeDto, String seriesId, int seasonNumber) {
        Season season = findOrCreateSeason(seriesId, seasonNumber);

        Episode episode = new Episode();
        episode.setEpisodeNumber(episodeDto.getEpisodeNumber());
        episode.setEpisodeTitle(episodeDto.getEpisodeTitle());
        episode.setMediaUrl(episodeDto.getMediaUrl());
        episode.setImageUrl(episodeDto.getImageUrl());
        episode.setDurationMinutes(episodeDto.getDurationMinutes());
        episode.setEpisodeDescription(episodeDto.getEpisodeDescription());
        episode.setSeason(season);

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

            series.getSeasons().add(season);
            seriesRepository.save(series);
        }

        return season;
    }

    public EpisodeDto getEpisodeById(Long episodeId) {
        Episode episode = episodeRepository.findEpisodeById(episodeId);
        return EpisodeDtoMapper.map(episode);
    }

    public EpisodeInfoDto getEpisodeInfo(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId).orElse(null);
        if (episode == null) {
            return null; // Możesz też rzucić wyjątek, jeśli to preferujesz
        }

        Season season = episode.getSeason();
        Series series = season != null ? season.getSeries() : null;

        EpisodeInfoDto episodeInfoDto = new EpisodeInfoDto();
        episodeInfoDto.setSerialTitle(series != null ? series.getTitle() : "Nieznany serial");
        episodeInfoDto.setSeasonNumber(season != null ? season.getSeasonNumber() : -1);
        episodeInfoDto.setEpisodeNumber(episode.getEpisodeNumber());
        episodeInfoDto.setMediaUrl(episode.getMediaUrl());

        return episodeInfoDto;
    }
}
