package pl.puccini.viaplay.domain.series.dto.seriesDto;

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
    private String backgroundImageUrl;
    private String description;
    private String staff;
    private String languages;
    private String genre;
    private boolean promoted;
    private int ageLimit;
    private double imdbRating;
    private String imdbUrl;
    private int seasonsCount;
}
