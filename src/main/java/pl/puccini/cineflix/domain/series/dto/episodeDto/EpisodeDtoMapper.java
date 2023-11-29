package pl.puccini.cineflix.domain.series.dto.episodeDto;

import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.series.model.Episode;
@Getter
@Setter
public class EpisodeDtoMapper {
    public static EpisodeDto map(Episode episode){
        EpisodeDto episodeDto = new EpisodeDto();
        episodeDto.setId(episode.getId());
        episodeDto.setEpisodeNumber(episode.getEpisodeNumber());
        episodeDto.setEpisodeTitle(episode.getEpisodeTitle());
        episodeDto.setMediaUrl(episode.getMediaUrl());
        episodeDto.setImageUrl(episode.getImageUrl());
        episodeDto.setDurationMinutes(episode.getDurationMinutes());
        episodeDto.setEpisodeDescription(episode.getEpisodeDescription());
        episodeDto.setSeasonNumber(episode.getSeason().getSeasonNumber());
        episodeDto.setWatched(false);
        return episodeDto;
    }
}
