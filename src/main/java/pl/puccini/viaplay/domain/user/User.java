//package pl.puccini.viaplay.domain.user;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import pl.puccini.viaplay.domain.movie.model.Movie;
//import pl.puccini.viaplay.domain.movie.model.MovieRating;
import pl.puccini.viaplay.domain.series.model.Series;
//import pl.puccini.viaplay.domain.series.model.SeriesRating;

import java.util.List;
import java.util.Set;

//@Entity
//@Table(name = "users")
//@Getter
//@Setter
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;

//    @ManyToOne
//    @JoinColumn(name = "avatar_id")
//    private Avatar avatar;

//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "user_watchlist_movies",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "movie_id")
//    )
//    private List<Movie> watchlistMovies;
//
//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "user_watchlist_series",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "series_id")
//    )
//    private List<Series> watchlistSeries;

//    // Możemy przechowywać oceny w osobnych tabelach, gdzie klucz obcy będzie wskazywał na użytkownika
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<MovieRating> movieRatings;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<SeriesRating> seriesRatings;

    // Historię obejrzanych można zaimplementować jako relację wiele do wielu z dodatkowymi informacjami, np. datą obejrzenia
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<WatchedMovie> watchedMovies;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<WatchedSeries> watchedSeries;

//}
