package pl.puccini.cineflix.domain.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.puccini.cineflix.domain.user.model.UserRole;
@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    UserRole findByName(String user);
}
