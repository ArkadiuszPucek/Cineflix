package pl.puccini.cineflix.domain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.user.UserFacade;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;

import java.util.Optional;

@Component
public class UserUtils {

    private final UserFacade userFacade;
    private final UserRepository userRepository;

    public UserUtils(UserFacade userFacade, UserRepository userRepository) {
        this.userFacade = userFacade;
        this.userRepository = userRepository;
    }
    public void addAvatarUrlToModel(Authentication authentication, Model model) {
        if (authentication != null) {
            String currentUsername = authentication.getName();
            String avatarUrl = userFacade.getAvatarUrlByUsername(currentUsername);
            model.addAttribute("avatar", avatarUrl);
        }
    }
    public Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            Optional<User> userOpt = userRepository.findByEmail(email);
            return userOpt.map(User::getId).orElse(null);
        }
        return null;
    }

    public void deleteUserAndHandleLogout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, Long userId) {
        userFacade.deleteUserAndHandleLogout(request, response, redirectAttributes, userId);
    }
}
