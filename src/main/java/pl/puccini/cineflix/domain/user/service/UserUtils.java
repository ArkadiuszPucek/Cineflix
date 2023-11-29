package pl.puccini.cineflix.domain.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import pl.puccini.cineflix.domain.user.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Załóżmy, że nazwa użytkownika jest e-mailem, który jest unikatowy
            String email = userDetails.getUsername();
            // Następnie używamy serwisu użytkownika, aby pobrać obiekt użytkownika i zwrócić jego ID
            User user = userService.findByUsername(email);
            return user.getId();
        }
        return null; // Jeśli użytkownik nie jest zalogowany, zwracamy null
    }

    public String extractVideoId(String youtubeUrl) {
        Pattern pattern = Pattern.compile("https?://www\\.youtube\\.com/watch\\?v=([\\w-]+)");
        Matcher matcher = pattern.matcher(youtubeUrl);

        if (matcher.find()) {
            return "https://www.youtube.com/embed/" + matcher.group(1);
        }

        pattern = Pattern.compile("https?://youtu\\.be/([\\w-]+)");
        matcher = pattern.matcher(youtubeUrl);
        if (matcher.find()) {
            return "https://www.youtube.com/embed/" + matcher.group(1);
        }

        return null;
    }
}
