package pl.puccini.cineflix.domain.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.exceptions.UserRoleNotFoundException;
import pl.puccini.cineflix.domain.exceptions.UsernameExistsException;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.user.dto.UserCredentialsDto;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }

    public User registerNewUserAccount(UserDto userDto) throws UsernameExistsException {
        if (usernameExists(userDto.getUsername())) {
            throw new UsernameExistsException(
                    "Konto z podanym adresem e-mail istnieje w serwisie");
        }
        User user = new User();
        user.setEmail(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        UserRole role = userRoleRepository.findByName("USER");
        if (role == null){
            throw new UserRoleNotFoundException("Nie znaleziono roli użytkownika");
        }
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    private boolean usernameExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }

    public List<User> getAllUsersInService() {
        return userRepository.findAll();
    }

    public void changeUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika"));

        UserRole role = userRoleRepository.findByName(newRole);

        if (role == null){
            throw new UserRoleNotFoundException("Nie znaleziono roli użytkownika");
        }
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
    }


    public boolean deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("Nie znaleziono użytkownika"));
        if (user != null){
            userRepository.delete(user);
            return true;
        }else {
            return false;
        }
    }
}