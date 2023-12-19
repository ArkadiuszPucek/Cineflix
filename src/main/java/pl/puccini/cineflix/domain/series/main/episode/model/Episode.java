package pl.puccini.cineflix.domain.series.main.episode.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.series.main.season.model.Season;

@Entity
@Getter
@Setter
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int episodeNumber;
    private String episodeTitle;
    private String mediaUrl;
    private String imageUrl;
    private Integer durationMinutes;
    private String episodeDescription;

    @ManyToOne
    @JoinColumn(name = "season_id", referencedColumnName = "id")
    private Season season;
}
