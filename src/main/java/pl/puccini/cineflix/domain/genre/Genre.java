package pl.puccini.cineflix.domain.genre;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.series.model.Series;

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
