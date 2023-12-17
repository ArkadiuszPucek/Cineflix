package pl.puccini.cineflix.domain.user.userRatings.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.user.userDetails.model.User;


@Entity
@Table(name = "user_ratings")
@Getter
@Setter
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    @Column(name = "upvote")
    private boolean upvote;

}
