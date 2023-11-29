package pl.puccini.cineflix.domain.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.series.model.Episode;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ViewingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "imdbId")
    private Movie movie;

    private LocalDateTime viewedOn;
}
