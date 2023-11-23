package pl.puccini.cineflix.domain.user.repository;

import org.springframework.data.repository.CrudRepository;
import pl.puccini.cineflix.domain.user.model.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    UserRole findByName(String user);
}
