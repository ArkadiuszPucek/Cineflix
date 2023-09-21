package pl.puccini.viaplay.domain.series;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.viaplay.domain.genre.Genre;

@Entity
@Getter
@Setter
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int releaseYear;
    private String imageUrl;
    private String imdbRating;
    private int seasonsNumber;
    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private Genre genre;

}
