package pl.puccini.cineflix.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.*;
import pl.puccini.cineflix.domain.user.dto.UserCredentialsDto;
import pl.puccini.cineflix.domain.user.dto.UserDto;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.model.UserList;
import pl.puccini.cineflix.domain.user.model.UserRole;
import pl.puccini.cineflix.domain.user.repository.UserListRepository;
import pl.puccini.cineflix.domain.user.repository.UserRepository;
import pl.puccini.cineflix.domain.user.repository.UserRoleRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final UserListService userListService;
    private final UserListRepository userListRepository;

    private static final String DEFAULT_AVATAR_WOLF = "/images/avatars/wilk2.png";
    private static final String AVATAR_DIRECTORY = "/images/avatars/";



    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserRoleRepository userRoleRepository, UserListService userListService, UserListRepository userListRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.userListService = userListService;
        this.userListRepository = userListRepository;
    }

    public String getAvatarUrlByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(User::getAvatar)
                .orElseThrow(()-> new UserNotFoundException("Użytkownik nie istnieje"));
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
        user.setAvatar(DEFAULT_AVATAR_WOLF);

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
            userListService.removeUserAndAllItems(userId);
            userRepository.delete(user);
            return true;
        }else {
            return false;
        }
    }

    public void changePassword(String oldPassword, String newPassword, String confirmPassword, User user)
            throws InvalidPasswordException, PasswordConfirmationException, PasswordFormatException {

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Stare hasło jest niepoprawne.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordConfirmationException("Hasła nie są identyczne.");
        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.*\\d).{8,}$";
        if (!newPassword.matches(passwordRegex)) {
            throw new PasswordFormatException("Hasło musi zawierać co najmniej jedną dużą literę, jedną cyfrę, jeden znak specjalny i musi mieć co najmniej 8 znaków.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User findByUsername(String currentUserName) {
        return userRepository.findByEmail(currentUserName).orElseThrow(()->new UserNotFoundException("Nie znaleziono użytkownika"));
    }

    public User findUserById(Long userId) {
        return userRepository.findUserById(userId);
    }

    public void deleteUserByEmail(String email) {
        User user = findByUsername(email);
        userRepository.delete(user);
    }

    public void changeEmail(String oldEmail, String newEmail) {
        User user = findByUsername(oldEmail);
        if (user != null){
            user.setEmail(newEmail);
            userRepository.save(user);
        }else {
            throw new UserNotFoundException("Nie znaleziono użytkownika");
        }
    }

    public void changeAvatar(String username, String newAvatar) {
        User user = findByUsername(username);
        user.setAvatar(newAvatar);
        userRepository.save(user);
    }

    public List<String> getAvatarPaths() {
        File avatarDir = new File("src/main/resources/static" + AVATAR_DIRECTORY);
        File[] files = avatarDir.listFiles();
        List<String> avatarPaths = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".png")) {
                    avatarPaths.add(AVATAR_DIRECTORY + file.getName());
                }
            }
        }
        return avatarPaths;
    }

}