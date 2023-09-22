package pl.puccini.viaplay.domain.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class IMDbApiResponse {
    @JsonProperty("ratings")
    private IMDbData imdbRating;
}