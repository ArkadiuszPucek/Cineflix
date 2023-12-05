package pl.puccini.cineflix.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
            String email = userDetails.getUsername();
            User user = userService.findByUsername(email);
            return user.getId();
        }
        return null;
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

    public void deleteUser(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, Long userId) {
        boolean deleted = userService.deleteUserById(userId);
        if (deleted) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            redirectAttributes.addFlashAttribute("message", "User deleted.");
        } else {
            redirectAttributes.addFlashAttribute("message", "User not found.");
        }
    }
}
