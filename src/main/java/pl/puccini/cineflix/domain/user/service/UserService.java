package pl.puccini.cineflix.domain.user.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.*;
import pl.puccini.cineflix.domain.user.dto.UserCredentialsDto;
import pl.puccini.cineflix.domain.user.dto.UserDto;
import pl.puccini.cineflix.domain.user.model.User;
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
    private final ViewingHistoryService viewingHistoryService;

    private static final String DEFAULT_AVATAR_WOLF = "/images/avatars/wilk2.png";
    private static final String AVATAR_DIRECTORY = "/images/avatars/";



    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserRoleRepository userRoleRepository, UserListService userListService, @Lazy ViewingHistoryService viewingHistoryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.userListService = userListService;
        this.viewingHistoryService = viewingHistoryService;
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

    public void changePassword(String oldPassword, String newPassword, String confirmPassword, User user)
            throws InvalidPasswordException, PasswordConfirmationException, PasswordFormatException {

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Current password incorrect.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordConfirmationException("The passwords are not identical.");
        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.*\\d).{8,}$";
        if (!newPassword.matches(passwordRegex)) {
            throw new PasswordFormatException("The password must contain at least one uppercase letter, one number, one special character and must be at least 8 characters long.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User findByUsername(String currentUserName) {
        return userRepository.findByEmail(currentUserName).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    public User findUserById(Long userId) {
        return userRepository.findUserById(userId);
    }


    public boolean deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
        if (user != null){
            userListService.removeUserAndAllItems(userId);
            viewingHistoryService.removeUserAndAllViewingHistory(userId);
            userRepository.delete(user);
            return true;
        }else {
            return false;
        }
    }

    public void changeEmail(String oldEmail, String newEmail) {
        User user = findByUsername(oldEmail);
        if (user != null){
            user.setEmail(newEmail);
            userRepository.save(user);
        }else {
            throw new UserNotFoundException("User not found");
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