package pl.puccini.cineflix.web.admin;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.cineflix.domain.UserUtils;


@Controller
public class AdminMainPageController {
    private final UserUtils userUtils;

    public AdminMainPageController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @GetMapping("/admin")
    public String getAdminPanel(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);

        return "admin/admin";
    }
}
