package pl.puccini.cineflix.domain.user.userRole.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.UserRoleNotFoundException;
import pl.puccini.cineflix.domain.user.userRole.model.UserRole;
import pl.puccini.cineflix.domain.user.userRole.repository.UserRoleRepository;

@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }
    public UserRole findRoleByName(String roleName) {
        return userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new UserRoleNotFoundException("Role not found: " + roleName));
    }
}
