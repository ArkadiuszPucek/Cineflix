package pl.puccini.cineflix.domain.user.userRole.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.exceptions.UserRoleNotFoundException;
import pl.puccini.cineflix.domain.user.userRole.model.UserRole;
import pl.puccini.cineflix.domain.user.userRole.repository.UserRoleRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;
    @InjectMocks
    private UserRoleService userRoleService;

    private final String roleName = "USER";
    private UserRole role;

    @BeforeEach
    void setUp() {
        role = new UserRole();
        role.setName(roleName);
    }

    @Test
    void whenRoleIsFound_thenReturnsRole() {
        when(userRoleRepository.findByName(roleName)).thenReturn(Optional.of(role));

        UserRole foundRole = userRoleService.findRoleByName(roleName);

        assertEquals(roleName, foundRole.getName());
        verify(userRoleRepository).findByName(roleName);
    }

    @Test
    void whenRoleIsNotFound_thenThrowsException() {
        when(userRoleRepository.findByName(roleName)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserRoleNotFoundException.class,
                () -> userRoleService.findRoleByName(roleName)
        );

        String expectedMessage = "Role not found: " + roleName;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}