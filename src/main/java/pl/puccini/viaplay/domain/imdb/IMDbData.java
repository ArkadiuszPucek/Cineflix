package pl.puccini.viaplay.domain.imdb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private String staff; // Nie widać w JSON, musisz określić jak to zapisywać
    private String directedBy; // Nie widać w JSON, musisz określić jak to zapisywać
    private String languages; // Nie widać w JSON, musisz określić jak to zapisywać
    private String genre; // Z listy 'genres'
    private boolean promoted;
    private double imdbRating;
    private String imdbUrl;


}
