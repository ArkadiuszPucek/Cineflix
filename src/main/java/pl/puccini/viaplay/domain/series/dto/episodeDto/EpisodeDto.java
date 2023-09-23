package pl.puccini.viaplay.domain.series.dto.episodeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EpisodeDto {
    private Long id;
    private String episodeTitle;
    private String mediaUrl;
    private int durationMinutes;
    private String episodeDescription;
}
