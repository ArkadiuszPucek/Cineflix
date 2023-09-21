package pl.puccini.viaplay.domain.series.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.viaplay.domain.genre.Genre;

@AllArgsConstructor
@Getter
@Setter
public class SeriesDto {
    private Long id;
    private String title;
    private int releaseYear;
    private String imageUrl;
    private String imdbRating;
    private int seasonsNumber;
    private String  genre;
    private boolean promoted;
}
