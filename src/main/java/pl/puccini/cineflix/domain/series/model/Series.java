package pl.puccini.cineflix.domain.series.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.genre.Genre;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Series {
    @Id
    private String imdbId;
    private String title;
    private int releaseYear;
    private String imageUrl;
    private String backgroundImageUrl;
    private String description;
    private String staff;
    private boolean promoted;
    private int ageLimit;
    private double imdbRating;
    private String imdbUrl;
    private int seasonsCount;

    @ManyToOne
    @JoinColumn(name = "genre_type", referencedColumnName = "genre_type")
    private Genre genre;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons = new ArrayList<>();
}
