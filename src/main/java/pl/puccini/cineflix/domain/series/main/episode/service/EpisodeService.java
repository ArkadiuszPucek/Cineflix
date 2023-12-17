package pl.puccini.cineflix.domain.series.main.episode.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFactory;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDtoMapper;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeInfoDto;
import pl.puccini.cineflix.domain.series.main.season.Season;
import pl.puccini.cineflix.domain.series.main.season.SeasonService;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.episode.repository.EpisodeRepository;
import pl.puccini.cineflix.domain.user.viewingHistory.ViewingHistoryFacade;
import pl.puccini.cineflix.domain.user.viewingHistory.service.UserViewingHistoryService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final SeasonService seasonService;
    private final ViewingHistoryFacade viewingHistoryFacade;
    private final SeriesFacade seriesFacade;
    private final EpisodeFactory episodeFactory;


    public EpisodeService(EpisodeRepository episodeRepository, SeasonService seasonService, ViewingHistoryFacade viewingHistoryFacade, @Lazy SeriesFacade seriesFacade, EpisodeFactory episodeFactory) {
        this.episodeRepository = episodeRepository;
        this.seasonService = seasonService;
        this.viewingHistoryFacade = viewingHistoryFacade;
        this.seriesFacade = seriesFacade;
        this.episodeFactory = episodeFactory;
    }

    @Transactional
    public Episode addEpisode(EpisodeDto episodeDto, String seriesId, int seasonNumber) {
        Season season = seasonService.findOrCreateSeason(seriesId, seasonNumber);
        Episode episode = episodeFactory.createEpisode(episodeDto, season);
        return episodeRepository.save(episode);
    }

    public EpisodeDto getEpisodeById(Long episodeId) {
        return episodeRepository.findById(episodeId)
                .map(EpisodeDtoMapper::map)
                .orElseThrow(()-> new EpisodeNotFoundException("Episode not found"));
    }
    @Transactional
    public Episode updateEpisode(EpisodeDto episodeDto) {
        Episode existingEpisode = episodeRepository.findById(episodeDto.getId())
                .orElseThrow(() -> new EpisodeNotFoundException("Episode not found"));
        episodeFactory.mapDtoToEpisode(episodeDto, existingEpisode);
        episodeRepository.save(existingEpisode);
        return existingEpisode;
    }
    @Transactional
    public Episode deleteEpisodeById(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new EpisodeNotFoundException("Episode not found"));
        episodeRepository.delete(episode);
        return episode;
    }

    public Set<Long> getWatchedEpisodesIds(Long userId) {
        return viewingHistoryFacade.getWatchedEpisodeIds(userId);
    }

    public EpisodeDto findFirstUnwatchedEpisode(String seriesId, Long userId) {
        List<Season> seasons = seasonService.getSeasonsForSeries(seriesId);
        Set<Long> watchedEpisodeIds = getWatchedEpisodesIds(userId);

        for (Season season : seasons) {
            List<Episode> episodes = season.getEpisodes().stream()
                    .sorted(Comparator.comparingInt(Episode::getEpisodeNumber)).toList();

            for (Episode episode : episodes) {
                if (!watchedEpisodeIds.contains(episode.getId())) {
                    return EpisodeDtoMapper.map(episode);
                }
            }
        }
        if (!seasons.isEmpty() && !seasons.get(0).getEpisodes().isEmpty()) {
            return EpisodeDtoMapper.map(seasons.get(0).getEpisodes().get(0));
        }
        return null;
    }

    public Episode findEpisodeById(Long episodeId){
        return episodeRepository.findById(episodeId).orElseThrow(() -> new EpisodeNotFoundException("Episode Not Found"));
    }

    public Optional<EpisodeInfoDto> getEpisodeInfo(Long episodeId) {
        return episodeRepository.findById(episodeId).map(episode -> {
            Season season = episode.getSeason();
            Series series = season != null ? season.getSeries() : null;

            EpisodeInfoDto episodeInfoDto = new EpisodeInfoDto();
            episodeInfoDto.setImdbId(series != null ? series.getImdbId() : "");
            episodeInfoDto.setSerialTitle(series != null ? series.getTitle() : "Nieznany serial");
            episodeInfoDto.setSeasonNumber(season != null ? season.getSeasonNumber() : -1);
            episodeInfoDto.setEpisodeNumber(episode.getEpisodeNumber());
            episodeInfoDto.setMediaUrl(episode.getMediaUrl());

            return episodeInfoDto;
        });
    }

    public String processEpisodeAddition(EpisodeDto episodeDto, String seriesId, int seasonNumber, int episodeNumber, int seasonsCount, String action, String seriesTitle) {
        String normalizedTitle = seriesFacade.formatSeriesTitle(seriesTitle);
        if (!isSeasonNumberValid(seasonNumber, seasonsCount)) {
            return generateSeriesUrl(normalizedTitle);
        }

        addEpisode(episodeDto, seriesId, seasonNumber);
        return determineNextActionUrl(action, seriesId, seasonNumber, episodeNumber, seasonsCount, normalizedTitle);
    }

    private boolean isSeasonNumberValid(int seasonNumber, int seasonsCount) {
        return seasonNumber <= seasonsCount;
    }

    private String generateSeriesUrl(String normalizedTitle) {
        return "/series/" + normalizedTitle + "/sezon-1";
    }

    private String determineNextActionUrl(String action, String seriesId, int seasonNumber, int episodeNumber, int seasonsCount, String normalizedTitle) {
        if ("addEpisode".equals(action)) {
            return "/admin/add-episode/" + seriesId + "/" + seasonNumber + "/" + (episodeNumber + 1);
        } else if (seasonNumber < seasonsCount) {
            return "/admin/add-episode/" + seriesId + "/" + (seasonNumber + 1) + "/1";
        } else {
            return generateSeriesUrl(normalizedTitle);
        }
    }
}


