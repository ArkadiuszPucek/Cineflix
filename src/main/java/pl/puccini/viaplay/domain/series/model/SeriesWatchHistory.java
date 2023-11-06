//package pl.puccini.viaplay.domain.series.model;
//
//import jakarta.persistence.*;
//import pl.puccini.viaplay.domain.user.User;
//
//import java.time.LocalDateTime;
//
//
//@Entity
//@Table(name = "series_watch_history")
//public class SeriesWatchHistory {
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
//    @JoinColumn(name = "series_id")
//    private Series movie;
//
//    @ManyToOne
//    @JoinColumn(name = "episode_id")
//    private Episode episode;
//
//    private LocalDateTime watchedAt;
//}
