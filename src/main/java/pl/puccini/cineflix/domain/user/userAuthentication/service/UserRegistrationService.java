package pl.puccini.cineflix.domain.user.userAuthentication.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.exceptions.UsernameExistsException;
import pl.puccini.cineflix.domain.user.userDetails.dto.UserDto;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userRole.model.UserRole;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.userRole.repository.UserRoleRepository;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private static final String DEFAULT_AVATAR_WOLF = "/images/avatars/wilk2.png";
    private static final String ROLE_USER = "USER";

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public User registerNewUserAccount(UserDto userDto) throws UsernameExistsException {
        validateUsername(userDto.getUsername());

        User newUser = new User();
        newUser.setEmail(userDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setAvatar(DEFAULT_AVATAR_WOLF);

        UserRole role = userRoleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default user role not found"));
        newUser.getRoles().add(role);

        return userRepository.save(newUser);
    }

    private void validateUsername(String username) throws UsernameExistsException {
        if (userRepository.findByEmail(username).isPresent()) {
            throw new UsernameExistsException("An account with the provided email address already exists.");
        }
    }
}
