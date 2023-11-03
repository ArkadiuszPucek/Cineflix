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
    private String mediaType;
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
    private boolean promoted;
    private double imdbRating;
    private String imdbUrl;

    @ManyToOne
    @JoinColumn(name = "genre_type")
    private Genre genre;
}
