package pl.puccini.cineflix.domain.user.userDetails.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import pl.puccini.cineflix.domain.user.userRatings.model.UserRating;
import pl.puccini.cineflix.domain.user.userRole.model.UserRole;

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
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<UserRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRating> ratings;

    public String getRoleNames() {
        return roles.stream()
                .map(UserRole::getName)
                .collect(Collectors.joining(", "));
    }
}
