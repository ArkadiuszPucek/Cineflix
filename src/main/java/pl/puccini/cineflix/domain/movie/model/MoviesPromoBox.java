package pl.puccini.cineflix.domain.movie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Setter
@Getter
public class MoviesPromoBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String moviesPromoBoxTitle;
    private String imdbIds;
}
