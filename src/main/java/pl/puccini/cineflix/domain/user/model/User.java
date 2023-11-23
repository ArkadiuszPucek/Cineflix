package pl.puccini.cineflix.domain.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    @Length(min = 8, message = "Hasło musi mieć co najmniej 8 znaków.")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<UserRole> roles = new HashSet<>();

    public String getRoleNames() {
        return roles.stream()
                .map(UserRole::getName)
                .collect(Collectors.joining(", "));
    }
//
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
//
////     Historię obejrzanych można zaimplementować jako relację wiele do wielu z dodatkowymi informacjami, np. datą obejrzenia
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<WatchedMovie> watchedMovies;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<WatchedSeries> watchedSeries;

}
