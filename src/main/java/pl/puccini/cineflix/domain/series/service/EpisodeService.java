package pl.puccini.cineflix.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeInfoDto;
import pl.puccini.cineflix.domain.series.model.Season;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.repository.EpisodeRepository;
import pl.puccini.cineflix.domain.series.model.Episode;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final SeasonService seasonService;

    public EpisodeService(EpisodeRepository episodeRepository, SeasonService seasonService) {
        this.episodeRepository = episodeRepository;
        this.seasonService = seasonService;
    }


//    List<Episode> getAllEpisodes(){
//        return episodeRepository.findAllBy();
//    }

//    public List<EpisodeDto> getEpisodesForSeason(Long seasonId) {
//        List<Episode> episodes = episodeRepository.findBySeasonId(seasonId);
//
//        List<EpisodeDto> episodeDtos = episodes.stream()
//                .map(EpisodeDtoMapper::map)
//                .collect(Collectors.toList());
//
//        return episodeDtos;
//    }

    public Episode addEpisode(EpisodeDto episodeDto, String seriesId, int seasonNumber) {
        Season season = seasonService.findOrCreateSeason(seriesId, seasonNumber);
//        Season season = findOrCreateSeason(seriesId, seasonNumber);

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

//    private Season findOrCreateSeason(String seriesId, int seasonNumber) {
//        Series series = seriesRepository.findByImdbId(seriesId);
//        Season season = seasonRepository.findBySeriesAndSeasonNumber(series, seasonNumber);
//
//        if (season == null) {
//            season = new Season();
//            season.setSeries(series);
//            season.setSeasonNumber(seasonNumber);
//            season = seasonRepository.save(season);
//
//            series.getSeasons().add(season);
//            seriesRepository.save(series);
//        }
//
//        return season;
//    }

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
}


