package pl.puccini.viaplay.domain.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.viaplay.domain.genre.Genre;

@AllArgsConstructor
@Getter
@Setter
public class MovieDto {

    private String imdbId;
    private String title;
    private int releaseYear;
    private String imageUrl;
    private String backgroundImageUrl;
    private String mediaUrl;
    private int timeline;
    private int ageLimit;
    private String description;
    private String staff;
    private String directedBy;
    private String languages;
    private String genre;
    private boolean promoted;
    private double imdbRating;
    private String imdbUrl;
}
