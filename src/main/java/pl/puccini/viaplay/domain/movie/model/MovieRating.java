//package pl.puccini.viaplay.domain.movie.model;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.*;
//import jakarta.persistence.Table;
//import lombok.Getter;
//import lombok.Setter;
//import pl.puccini.viaplay.domain.user.User;
//
//@Entity
//@Table(name="movie_ratings")
//@Getter
//@Setter
//public class MovieRating {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "movie_id", nullable = false)
//    private Movie movie;
//
//    @Column(nullable = false)
//    private Boolean liked; // True dla łapki w górę, false dla łapki w dół
//
//}
