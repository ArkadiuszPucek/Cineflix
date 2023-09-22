package pl.puccini.viaplay.domain.series;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SeriesDto {
    private String imdbId;
    private String title;
    private int releaseYear;
    private String imageUrl;
    private String description;
    private String cast;
    private String languages;
    private String genre;
    private boolean promoted;
    private int season;
    private int ageLimit;
}
