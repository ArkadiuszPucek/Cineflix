package pl.puccini.cineflix.domain.user;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.user.dto.UserCredentialsDto;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
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