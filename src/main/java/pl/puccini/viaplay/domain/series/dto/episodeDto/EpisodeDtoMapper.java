package pl.puccini.viaplay.domain.series.dto.episodeDto;

import pl.puccini.viaplay.domain.series.model.Episode;

public class EpisodeDtoMapper {
    public static EpisodeDto map(Episode episode){
        return new EpisodeDto(
                episode.getId(),
                episode.getEpisodeTitle(),
                episode.getMediaUrl(),
                episode.getDurationMinutes(),
                episode.getEpisodeDescription()
        );
    }
}
