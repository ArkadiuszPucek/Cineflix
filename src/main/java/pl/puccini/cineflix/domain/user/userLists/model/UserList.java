package pl.puccini.cineflix.domain.user.userLists.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.user.userDetails.model.User;

@Entity
@Table(name = "user_list")
@Getter
@Setter
public class UserList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "imdb_id")
    private String imdbId;
}
