package pl.puccini.cineflix.config.promoBox.seriesPromoBox.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SeriesPromoBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String seriesPromoBoxTitle;
    private String imdbIds;
}
