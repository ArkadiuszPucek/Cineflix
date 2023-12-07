package pl.puccini.cineflix.domain.series.dto.seriesDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeriesDto {
    private String imdbId;
    private String title;
    private Integer releaseYear;
    private String imageUrl;
    private String backgroundImageUrl;
    private String description;
    private String staff;
    private String genre;
    private boolean promoted;
    private Integer ageLimit;
    private Double imdbRating;
    private String imdbUrl;
    private Integer seasonsCount;
    private Boolean onUserList;
    private Boolean userRating;
    private Long firstUnwatchedEpisodeId;
    private Integer rateUpCount;
    private Integer rateDownCount;
}
