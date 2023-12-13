package pl.puccini.cineflix.config.carousel.series.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SeriesCarouselConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "active_genres", length = 1024)
    private String activeGenres;
}
