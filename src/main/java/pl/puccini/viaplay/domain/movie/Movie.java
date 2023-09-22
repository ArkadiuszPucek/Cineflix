package pl.puccini.viaplay.domain.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.viaplay.domain.genre.Genre;


@Entity
@Getter
@Setter
public class Movie {
    @Id
    private String imdbId;
    private String title;
    private int releaseYear;
    private String imageUrl;
    private String mediaUrl;
    private String timeline;
    private int ageLimit;
    private String description;
    private String cast;
    private String directedBy;
    private String languages;
    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private Genre genre;
    private boolean promoted;
}
