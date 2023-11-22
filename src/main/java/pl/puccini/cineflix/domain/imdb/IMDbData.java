package pl.puccini.cineflix.domain.imdb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class IMDbData {

    private String imdbId;
    private String title;
    private Integer releaseYear;
    private String imageUrl;
    private String backgroundImageUrl;
    private String mediaUrl;
    private Integer timeline;
    private Integer ageLimit;
    private String description;
    private String staff;
    private String directedBy;
    private String languages;
    private String genre;
    private boolean promoted;
    private double imdbRating;
    private String imdbUrl;


}
