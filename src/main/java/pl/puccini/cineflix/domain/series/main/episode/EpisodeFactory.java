package pl.puccini.cineflix.domain.series.main.episode;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.season.model.Season;

@Service
public class EpisodeFactory {
    public Episode createEpisode(EpisodeDto episodeDto, Season season) {
        if (episodeDto == null || season == null) {
            throw new IllegalArgumentException("EpisodeDto and Season cannot be null");
        }

        Episode episode = new Episode();
        mapDtoToEpisode(episodeDto, episode);
        episode.setSeason(season);

        return episode;
    }

    public void mapDtoToEpisode(EpisodeDto episodeDto, Episode episode) {
        if (episodeDto == null || episode == null) {
            throw new IllegalArgumentException("EpisodeDto and Episode cannot be null");
        }
        episode.setEpisodeNumber(episodeDto.getEpisodeNumber());
        episode.setEpisodeTitle(episodeDto.getEpisodeTitle());
        episode.setMediaUrl(episodeDto.getMediaUrl());
        episode.setImageUrl(episodeDto.getImageUrl());
        episode.setDurationMinutes(episodeDto.getDurationMinutes());
        episode.setEpisodeDescription(episodeDto.getEpisodeDescription());
    }
}
