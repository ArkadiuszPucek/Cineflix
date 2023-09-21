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
    private String imdbRating;
    private String genre;
    private boolean promoted;
}
