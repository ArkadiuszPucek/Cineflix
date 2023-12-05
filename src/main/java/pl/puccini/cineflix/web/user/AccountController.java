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
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.service.UserService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

@Controller
public class AccountController {

    private final UserService userService;
    private final UserUtils userUtils;

    public AccountController(UserService userService, UserUtils userUtils) {
        this.userService = userService;
        this.userUtils = userUtils;
    }

    @GetMapping("/account")
    public String account(Model model,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          Authentication authentication) {
        if (authentication != null) {
            String currentUserName = authentication.getName();
            userUtils.addAvatarUrlToModel(authentication, model);

            User user = userService.findByUsername(currentUserName);
            model.addAttribute("user", user);
        }else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "/admin/users/account-manage";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model, Authentication authentication) {
        userUtils.addAvatarUrlToModel(authentication, model);
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
            redirectAttributes.addFlashAttribute("success", "Password has been changed.");
        } catch (InvalidPasswordException | PasswordConfirmationException | PasswordFormatException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/change-password";
        }
        return "redirect:/account";
    }

    @GetMapping("/delete-account")
    public String deleteAccount(Authentication authentication,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        userUtils.addAvatarUrlToModel(authentication, model);

        if (userId != null) {
            userUtils.deleteUser(request, response, redirectAttributes, userId);
        } else {
            redirectAttributes.addFlashAttribute("message", "An error occurred while deleting the user");
            return "redirect:/account";
        }
        return "redirect:/login?accountDeleted";

    }


    @GetMapping("/change-email")
    public String changeEmailForm(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        userUtils.addAvatarUrlToModel(authentication, model);
        model.addAttribute("user", user);
        return "/admin/users/change-email-form";
    }

    @PostMapping("/change-email")
    public String changeEmail(@RequestParam String newEmail,
                                 Authentication authentication,
                              HttpServletRequest request,
                              HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {

        try {
            userService.changeEmail(authentication.getName(), newEmail);
            redirectAttributes.addFlashAttribute("message", "Email has been changed, please log in again.");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            return "redirect:/login";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/change-email";
        }
    }
}

