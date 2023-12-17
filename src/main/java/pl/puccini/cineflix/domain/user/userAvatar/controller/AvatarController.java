package pl.puccini.cineflix.domain.user.userAvatar.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.user.UserFacade;
import pl.puccini.cineflix.domain.UserUtils;

import java.util.List;

@Controller
public class AvatarController {

    private final UserUtils userUtils;
    private final UserFacade userFacade;

    public AvatarController(UserUtils userUtils, UserFacade userFacade) {
        this.userUtils = userUtils;
        this.userFacade = userFacade;
    }

    @GetMapping("/change-avatar")
    public String changeAvatarForm(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        List<String> avatarPaths = userFacade.getAvailableAvatarPaths();
        model.addAttribute("avatars", avatarPaths);
        return "admin/users/change-avatar-form";
    }

    @PostMapping("/change-avatar")
    public String changeAvatar(@RequestParam String selectedAvatar, Authentication authentication, RedirectAttributes redirectAttributes) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);        try {
            userFacade.changeAvatar(userId, selectedAvatar);
            redirectAttributes.addFlashAttribute("success", "The avatar has been changed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while changing the avatar: " + e.getMessage());
        }
        return "redirect:/account";
    }

}
