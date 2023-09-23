package pl.puccini.viaplay.domain.movie.model;

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
    private String staff;
    private String directedBy;
    private String languages;
    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "genreType")
    private Genre genre;
    private boolean promoted;
    private double imdbRating;
    private String imdbUrl;
}
