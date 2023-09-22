package pl.puccini.viaplay.domain.series;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.series.seasons.Season;

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
    private String cast;
    private String languages;
    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private Genre genre;
    private boolean promoted;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons = new ArrayList<>();
    private int ageLimit;

    // Getters and setters for seasons
}

