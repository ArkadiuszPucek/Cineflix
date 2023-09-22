package pl.puccini.viaplay.domain.series.episodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EpisodeDto {
    private Long id;
    private int episodeNumber;
    private String episodeTitle;
    private String urlLink;
    private int durationMinutes;
}
