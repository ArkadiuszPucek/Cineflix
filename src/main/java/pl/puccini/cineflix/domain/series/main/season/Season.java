package pl.puccini.cineflix.domain.series.main.season;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.series.model.Series;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int seasonNumber;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "series_id", referencedColumnName = "imdbId")
    private Series series;

}




