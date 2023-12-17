package pl.puccini.cineflix.domain.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.exceptions.InvalidPasswordException;
import pl.puccini.cineflix.domain.exceptions.PasswordConfirmationException;
import pl.puccini.cineflix.domain.exceptions.PasswordFormatException;
import pl.puccini.cineflix.domain.exceptions.UsernameExistsException;
import pl.puccini.cineflix.domain.user.userDetails.dto.UserDto;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.service.UserService;
import pl.puccini.cineflix.domain.user.userAuthentication.service.UserAuthenticationService;
import pl.puccini.cineflix.domain.user.userAuthentication.service.UserRegistrationService;
import pl.puccini.cineflix.domain.user.userAvatar.service.UserAvatarService;

import java.util.List;
import java.util.Optional;

@Service
public class UserFacade {
    private final UserService userService;
    private final UserRegistrationService userRegistrationService;
    private final UserAvatarService userAvatarService;
    private final UserAuthenticationService userAuthenticationService;

    public UserFacade(UserService userService, UserRegistrationService userRegistrationService, UserAvatarService userAvatarService, UserAuthenticationService userAuthenticationService) {
        this.userService = userService;
        this.userRegistrationService = userRegistrationService;
        this.userAvatarService = userAvatarService;
        this.userAuthenticationService = userAuthenticationService;
    }
    public void changeUserRole(Long userId, String newRole) {
        userService.changeUserRole(userId, newRole);
    }

    public String getAvatarUrlByUsername(String username) {
        return userService.getAvatarUrlByUsername(username);
    }

    public List<User> getAllUsersInService() {
        return userService.getAllUsersInService();
    }

    public User findByUsername(String currentUserName) {
        return userService.findByUsername(currentUserName);
    }
    public void changeEmail(String oldEmail, String newEmail) {
        userService.changeEmail(oldEmail, newEmail);
    }

    public void registerNewUserAccount(UserDto userDto) throws UsernameExistsException {
        userRegistrationService.registerNewUserAccount(userDto);
    }
    public void changeAvatar(Long userId, String newAvatar) {
        userAvatarService.changeAvatar(userId, newAvatar);
    }

    public List<String> getAvailableAvatarPaths() {
        return userAvatarService.getAvailableAvatarPaths();
    }

    public void changePassword(String oldPassword, String newPassword, String confirmPassword, Long userId) throws PasswordFormatException, InvalidPasswordException, PasswordConfirmationException {
        userAuthenticationService.changePassword(oldPassword, newPassword, confirmPassword, userId);
    }

    public void deleteUserAndHandleLogout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, Long userId) {
        userService.deleteUserAndHandleLogout(request, response, redirectAttributes, userId);
    }

    public void deleteUser(Long userId){
        userService.deleteUserById(userId);
    }
}
