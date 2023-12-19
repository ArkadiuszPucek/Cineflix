package pl.puccini.cineflix.domain.genre.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.puccini.cineflix.domain.series.main.series.model.Series;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genre")
@Setter
@Getter
public class Genre {
    @Id
    @Column(name = "genre_type")
    private String genreType;

    @OneToMany(mappedBy = "genre")
    private Set<Series> series = new HashSet<>();

}
