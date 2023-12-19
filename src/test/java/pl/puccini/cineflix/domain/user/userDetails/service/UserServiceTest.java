package pl.puccini.cineflix.domain.user.userDetails.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.userLists.service.UserListService;
import pl.puccini.cineflix.domain.user.userRole.model.UserRole;
import pl.puccini.cineflix.domain.user.userRole.service.UserRoleService;
import pl.puccini.cineflix.domain.user.viewingHistory.ViewingHistoryFacade;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserListService userListService;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private ViewingHistoryFacade viewingHistoryFacade;

    @InjectMocks
    private UserService userService;

    private final Long userId = 1L;
    private final String username = "test@example.com";
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail(username);
    }

    @Test
    void whenDeletingExistingUser_thenUserShouldBeDeleted() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertTrue(userService.deleteUserById(userId));
        verify(userRepository).delete(testUser);
    }

    @Test
    void whenDeletingNonExistingUser_thenThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteUserById(userId));
    }

    @Test
    void whenFindingUserByUsername_andUserExists_thenReturnUser() {
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(testUser));

        User foundUser = userService.findByUsername(username);
        assertEquals(testUser, foundUser);
    }

    @Test
    void whenFindingUserByUsername_andUserDoesNotExist_thenThrowException() {
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findByUsername(username));
    }

    @Test
    void whenChangingUserRole_andUserExists_thenUserRoleShouldBeUpdated() {
        UserRole newRole = new UserRole();
        newRole.setName("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRoleService.findRoleByName("ADMIN")).thenReturn(newRole);

        userService.changeUserRole(userId, "ADMIN");

        assertTrue(testUser.getRoles().contains(newRole));
        verify(userRepository).save(testUser);
    }

    @Test
    void whenChangingUserRole_andUserDoesNotExist_thenThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.changeUserRole(userId, "ADMIN"));
    }

    @Test
    void whenGettingAvatarUrlByUsername_andUserExists_thenReturnAvatarUrl() {
        String avatarUrl = "path/to/avatar.png";
        testUser.setAvatar(avatarUrl);

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(testUser));

        String result = userService.getAvatarUrlByUsername(username);
        assertEquals(avatarUrl, result);
    }

    @Test
    void whenGettingAvatarUrlByUsername_andUserDoesNotExist_thenThrowException() {
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getAvatarUrlByUsername(username));
    }

    @Test
    void whenGettingAllUsersInService_thenReturnAllUsers() {
        List<User> users = List.of(testUser);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsersInService();
        assertFalse(result.isEmpty());
        assertEquals(users.size(), result.size());
        assertEquals(testUser, result.get(0));
    }

    @Test
    void whenChangingEmail_andUserExists_thenEmailShouldBeUpdated() {
        String newEmail = "newemail@example.com";

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(testUser));

        userService.changeEmail(username, newEmail);

        assertEquals(newEmail, testUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void whenChangingEmail_andUserDoesNotExist_thenThrowException() {
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.changeEmail(username, "newemail@example.com"));
    }
}