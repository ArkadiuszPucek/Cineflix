package pl.puccini.viaplay.domain.series.dto.episodeDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EpisodeInfoDto {
    private String serialTitle;
    private Integer seasonNumber;
    private Integer episodeNumber;
    private String mediaUrl;
}
