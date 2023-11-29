package pl.puccini.cineflix.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeInfoDto;
import pl.puccini.cineflix.domain.series.model.Episode;
import pl.puccini.cineflix.domain.series.model.Season;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.repository.EpisodeRepository;
import pl.puccini.cineflix.domain.series.repository.SeasonRepository;
import pl.puccini.cineflix.domain.user.service.ViewingHistoryService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final SeasonService seasonService;
    private final ViewingHistoryService viewingHistoryService;
    private final SeasonRepository seasonRepository;

    public EpisodeService(EpisodeRepository episodeRepository, SeasonService seasonService, ViewingHistoryService viewingHistoryService, SeasonRepository seasonRepository) {
        this.episodeRepository = episodeRepository;
        this.seasonService = seasonService;
        this.viewingHistoryService = viewingHistoryService;
        this.seasonRepository = seasonRepository;
    }


    public Episode addEpisode(EpisodeDto episodeDto, String seriesId, int seasonNumber) {
        Season season = seasonService.findOrCreateSeason(seriesId, seasonNumber);

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

    public EpisodeDto getEpisodeById(Long episodeId) {
        Episode episode = episodeRepository.findEpisodeById(episodeId);
        return EpisodeDtoMapper.map(episode);
    }


    public EpisodeInfoDto getEpisodeInfo(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId).orElse(null);
        if (episode == null) {
            return null;
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

    public String processEpisodeAddition(EpisodeDto episodeDto, String seriesId, int seasonNumber, int episodeNumber, int seasonsCount, String action, String seriesTitle) {
        String normalizedTitle = normalizeTitle(seriesTitle);
        if (seasonNumber <= seasonsCount) {
            addEpisode(episodeDto, seriesId, seasonNumber);

            if ("addEpisode".equals(action)) {
                return "/admin/add-episode/" + seriesId + "/" + seasonNumber + "/" + (episodeNumber + 1);
            } else {
                if (seasonNumber < seasonsCount) {
                    return "/admin/add-episode/" + seriesId + "/" + (seasonNumber + 1) + "/1";
                } else {
                    return "/series/" + normalizedTitle + "/sezon-1";
                }
            }
        } else {
            return "/series/" + normalizedTitle + "/sezon-1";
        }
    }

    private String normalizeTitle(String title) {
        return title.toLowerCase().replace(" ", "-");
    }

    public Episode updateEpisode(EpisodeDto episodeDto) {
        Episode existingEpisode = episodeRepository.findEpisodeById(episodeDto.getId());

        if (existingEpisode == null) {
            throw new EpisodeNotFoundException("Nie znaleziono epizodu o ID: " + episodeDto.getId());
        }
        existingEpisode.setEpisodeNumber(episodeDto.getEpisodeNumber());
        existingEpisode.setEpisodeTitle(episodeDto.getEpisodeTitle());
        existingEpisode.setImageUrl(episodeDto.getImageUrl());
        existingEpisode.setMediaUrl(episodeDto.getMediaUrl());
        existingEpisode.setDurationMinutes(episodeDto.getDurationMinutes());
        existingEpisode.setEpisodeDescription(episodeDto.getEpisodeDescription());
        episodeRepository.save(existingEpisode);
        return existingEpisode;
    }

    public Episode deleteEpisodeById(Long episodeId) {
        Episode episodeById = episodeRepository.findEpisodeById(episodeId);
        if (episodeById != null){
            episodeRepository.delete(episodeById);
            return episodeById;
        }else {
            throw new EpisodeNotFoundException("Nie znaleziono epizodu o ID: " + episodeId);
        }
    }

    public Episode findEpisodeById(Long episodeId){
        return episodeRepository.findEpisodeById(episodeId);
    }

//    public List<EpisodeDto> getWatchedEpisodes(Long userId) {
//        List<ViewingHistory> historyList = viewingHistoryRepository.findByUserIdOrderByViewedOnDesc(userId);
//
//        // Mapuj na DTO
//        return historyList.stream()
//                .map(ViewingHistory::getEpisode)
//                .distinct() // Usuń duplikaty, jeśli użytkownik oglądał ten sam epizod wielokrotnie
//                .map(EpisodeDtoMapper::map)
//                .collect(Collectors.toList());
//    }



    public EpisodeDto findFirstUnwatchedEpisode(String seriesId, Long userId) {
        // Znajdź wszystkie sezony dla danego serialu
        List<Season> seasons = seasonRepository.findSeasonsBySeriesImdbId(seriesId);

        // Pobierz ID obejrzanych epizodów
        Set<Long> watchedEpisodeIds = getWatchedEpisodesIds(userId);

        for (Season season : seasons) {
            List<Episode> episodes = season.getEpisodes().stream()
                    .sorted(Comparator.comparingInt(Episode::getEpisodeNumber)).toList();

            for (Episode episode : episodes) {
                if (!watchedEpisodeIds.contains(episode.getId())) {
                    // Zwróć pierwszy nieobejrzany epizod
                    return EpisodeDtoMapper.map(episode);
                }
            }
        }

        // Jeśli wszystkie epizody zostały obejrzane, zwróć pierwszy epizod pierwszego sezonu
        if (!seasons.isEmpty() && !seasons.get(0).getEpisodes().isEmpty()) {
            return EpisodeDtoMapper.map(seasons.get(0).getEpisodes().get(0));
        }

        // Zwróć null lub rzuć wyjątek, jeśli serial nie ma epizodów
        return null;
    }

    public Set<Long> getWatchedEpisodesIds(Long userId) {
        return viewingHistoryService.getWatchedEpisodeIds(userId);
    }



}


