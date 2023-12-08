package pl.puccini.cineflix.domain.series.dto.episodeDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EpisodeInfoDto {
    private String imdbId;
    private String serialTitle;
    private Integer seasonNumber;
    private Integer episodeNumber;
    private String mediaUrl;
}
