package pl.puccini.cineflix.domain.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.UserRoleNotFoundException;
import pl.puccini.cineflix.domain.exceptions.UsernameExistsException;
import pl.puccini.cineflix.domain.user.dto.UserCredentialsDto;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

        UserRole role = roleRepository.findByName("USER");
        if (role == null){
            throw new UserRoleNotFoundException("Role not found");
        }
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    private boolean usernameExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }


//    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//
//    public User createUser(String username, String password, Avatar avatar) {
//        User user = new User();
//        user.setUsername(username);
//        user.setEncryptedPassword(bCryptPasswordEncoder.encode(password));
//        user.setAvatar(avatar);
//        // Zapisz użytkownika w bazie danych
//        return user;
//    }
}