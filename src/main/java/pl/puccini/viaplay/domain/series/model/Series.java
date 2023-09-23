package pl.puccini.viaplay.domain.series.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.series.model.Season;

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
    private String description;
    private String staff;
    private String languages;
    private boolean promoted;
    private int ageLimit;
    private double imdbRating;
    private String imdbUrl;
    private int seasonsCount;

    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "genreType")
    private Genre genre;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons = new ArrayList<>();
}

