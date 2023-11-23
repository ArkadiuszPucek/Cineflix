package pl.puccini.cineflix.domain.user.service;

import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.model.UserRole;
import pl.puccini.cineflix.domain.user.dto.UserCredentialsDto;

import java.util.Set;
import java.util.stream.Collectors;

public class UserCredentialsDtoMapper {
    static UserCredentialsDto map(User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        Set<String> roles = user.getRoles()
                .stream()
                .map(UserRole::getName)
                .collect(Collectors.toSet());
        return new UserCredentialsDto(email, password, roles);
    }
}
