package pl.puccini.cineflix.domain.series.dto.episodeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EpisodeDto {
    private Long id;
    private int episodeNumber;
    private String episodeTitle;
    private String mediaUrl;
    private String imageUrl;
    private Integer durationMinutes;
    private String episodeDescription;

}
