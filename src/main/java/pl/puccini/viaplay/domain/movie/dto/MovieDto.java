package pl.puccini.viaplay.domain.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.viaplay.domain.genre.Genre;

@AllArgsConstructor
@Getter
@Setter
public class MovieDto {

    private Long id;
    private String title;
    private int releaseYear;
    private String imageUrl;
    private String mediaUrl;
    private String imdbRating;
    private String timeline;
    private int ageLimit;
    private String description;
    private String cast;
    private String directedBy;
    private String languages;
    private String genre;
    private boolean promoted;
}
