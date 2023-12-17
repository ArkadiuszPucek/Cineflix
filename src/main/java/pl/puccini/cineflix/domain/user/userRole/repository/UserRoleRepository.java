package pl.puccini.cineflix.domain.user.userRole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.puccini.cineflix.domain.user.userRole.model.UserRole;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    Optional<UserRole> findByName(String user);
}
