package pl.puccini.cineflix.domain.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<UserRole, Long> {
    UserRole findByName(String user);
}
