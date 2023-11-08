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

    @JsonProperty("ratings")
    private double imdbRating;

    @JsonProperty("title")
    private String title;

    private String imdbUrl;

    private Integer releaseYear;
    private String imageUrl;
    private Integer timeline;
    private String description;


}
