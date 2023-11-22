package pl.puccini.cineflix.domain.series.dto.episodeDto;

import pl.puccini.cineflix.domain.series.model.Episode;

public class EpisodeDtoMapper {
    public static EpisodeDto map(Episode episode){
        return new EpisodeDto(
                episode.getId(),
                episode.getEpisodeNumber(),
                episode.getEpisodeTitle(),
                episode.getMediaUrl(),
                episode.getImageUrl(),
                episode.getDurationMinutes(),
                episode.getEpisodeDescription()
        );
    }
}
