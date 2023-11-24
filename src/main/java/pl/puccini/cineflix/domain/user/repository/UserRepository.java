package pl.puccini.cineflix.domain.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.puccini.cineflix.domain.user.model.User;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User findUserById(Long userId);
    List<User> findAll();

}
