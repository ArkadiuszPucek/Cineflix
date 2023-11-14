package pl.puccini.viaplay.domain.series.dto.seriesDto;

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
    private String languages;
    private String genre;
    private boolean promoted;
    private Integer ageLimit;
    private Double imdbRating;
    private String imdbUrl;
    private Integer seasonsCount;

}
