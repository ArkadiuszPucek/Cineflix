package pl.puccini.cineflix.domain.user.userAvatar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAvatarServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAvatarService userAvatarService;


    @Test
    void changeAvatarWhenUserExistingUser() {
        Long userId = 1L;
        String newAvatar = "newAvatar.png";
        User user = new User();
        user.setId(userId);
        user.setAvatar("oldAvatar.png");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userAvatarService.changeAvatar(userId, newAvatar);

        assertEquals(newAvatar, user.getAvatar());
        verify(userRepository).save(user);
    }

    @Test
    void changeAvatar_WhenNonExistingUser() {
        Long userId = 1L;
        String newAvatar = "newAvatar.png";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userAvatarService.changeAvatar(userId, newAvatar));
    }
}