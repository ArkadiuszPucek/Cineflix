package pl.puccini.viaplay.domain.series.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private int durationMinutes;
    private String episodeDescription;

    @ManyToOne
    @JoinColumn(name = "season_id", referencedColumnName = "id")
    private Season season;
}
