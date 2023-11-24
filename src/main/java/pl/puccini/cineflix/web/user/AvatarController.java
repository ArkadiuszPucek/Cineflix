package pl.puccini.cineflix.web.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.user.service.UserService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AvatarController {

    private final UserService userService;
    private final UserUtils userUtils;

    public AvatarController(UserService userService, UserUtils userUtils) {
        this.userService = userService;
        this.userUtils = userUtils;
    }

    @GetMapping("/change-avatar")
    public String changeAvatarForm(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        List<String> avatarPaths = userService.getAvatarPaths();
        model.addAttribute("avatars", avatarPaths);
        return "admin/users/change-avatar-form";
    }



    @PostMapping("/change-avatar")
    public String changeAvatar(@RequestParam String selectedAvatar, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            userService.changeAvatar(username, selectedAvatar);
            redirectAttributes.addFlashAttribute("success", "Avatar został zmieniony.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zmiany avatara: " + e.getMessage());
        }
        return "redirect:/account";
    }

}
