package pl.puccini.cineflix.web.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.exceptions.InvalidPasswordException;
import pl.puccini.cineflix.domain.exceptions.PasswordConfirmationException;
import pl.puccini.cineflix.domain.exceptions.PasswordFormatException;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.service.UserService;

@Controller
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public String account(Model model, Authentication authentication) {
        if (authentication != null) {
            String currentUserName = authentication.getName();

            User user = userService.findByUsername(currentUserName);
            model.addAttribute("user", user);
        }
        return "/admin/users/account-manage";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "/admin/users/change-password-form";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        try {
            User currentUser = userService.findByUsername(authentication.getName());
            userService.changePassword(oldPassword, newPassword, confirmPassword, currentUser);
            redirectAttributes.addFlashAttribute("success", "Hasło zostało zmienione.");
        } catch (InvalidPasswordException | PasswordConfirmationException | PasswordFormatException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/change-password";
        }
        return "redirect:/account";
    }

    @GetMapping("/delete-account")
    public String deleteAccount(Authentication authentication) {
        if (authentication != null) {
            String userEmail = authentication.getName();
            userService.deleteUserByEmail(userEmail);
        }
        return "redirect:/login";

    }
    @PostMapping("/delete-account")
    public String deleteAccount(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        userService.deleteUserByEmail(authentication.getName());

        // Wylogowanie użytkownika
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // Przekierowanie na stronę logowania z komunikatem
        return "redirect:/login?accountDeleted";
    }

    @PostMapping("/change-avatar")
    public String changeAvatar(@RequestParam("avatar") MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes) {
        // Logika zmiany avatara
        return "redirect:/account";
    }
}

