package pl.puccini.cineflix.domain.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class UserUtils {

    private final UserService userService;

    public UserUtils(UserService userService) {
        this.userService = userService;
    }

    public void addAvatarUrlToModel(Authentication authentication, Model model) {
        if (authentication != null) {
            String currentUsername = authentication.getName();
            String avatarUrl = userService.getAvatarUrlByUsername(currentUsername);
            model.addAttribute("avatar", avatarUrl);
        }
    }
}
