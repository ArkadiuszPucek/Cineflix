package pl.puccini.cineflix.domain.series.main.episode;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeInfoDto;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.episode.service.EpisodeService;

import java.util.Optional;
import java.util.Set;

@Service
public class EpisodeFacade {
    private final EpisodeService episodeService;

    public EpisodeFacade(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }
    public Episode addEpisode(EpisodeDto episodeDto, String seriesId, int seasonNumber) {
        return episodeService.addEpisode(episodeDto,seriesId,seasonNumber);
    }
    public EpisodeDto getEpisodeById(Long episodeId){
        return episodeService.getEpisodeById(episodeId);
    }
    public Episode updateEpisode(EpisodeDto episodeDto) {
        return episodeService.updateEpisode(episodeDto);
    }
    public Episode deleteEpisodeById(Long episodeId) {
        return episodeService.deleteEpisodeById(episodeId);
    }
    public Set<Long> getWatchedEpisodesIds(Long userId) {
        return episodeService.getWatchedEpisodesIds(userId);
    }
    public EpisodeDto findFirstUnwatchedEpisode(String seriesId, Long userId) {
        return episodeService.findFirstUnwatchedEpisode(seriesId, userId);
    }
    public Optional<EpisodeInfoDto> getEpisodeInfo(Long episodeId) {
        return episodeService.getEpisodeInfo(episodeId);
    }
    public String processEpisodeAddition(EpisodeDto episodeDto, String seriesId, int seasonNumber, int episodeNumber, int seasonsCount, String action, String seriesTitle) {
        return episodeService.processEpisodeAddition(episodeDto, seriesId, seasonNumber, episodeNumber, seasonsCount, action, seriesTitle);
    }
}
