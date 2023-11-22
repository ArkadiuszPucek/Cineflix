package pl.puccini.cineflix.domain.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    UserRole findByName(String user);
}
