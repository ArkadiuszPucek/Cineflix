package pl.puccini.cineflix.domain.user.userAvatar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAvatarService {
    private final UserRepository userRepository;
    private static final String AVATAR_DIRECTORY = "/images/avatars/";

    public UserAvatarService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void changeAvatar(Long userId, String newAvatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setAvatar(newAvatar);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableAvatarPaths() {
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
