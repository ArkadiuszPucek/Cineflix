package pl.puccini.cineflix.web.support;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.cineflix.domain.UserUtils;

@Controller
public class SupportController {
    private final UserUtils userUtils;

    public SupportController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @GetMapping("/support")
    public String supportPage(Authentication authentication, Model model){
        userUtils.addAvatarUrlToModel(authentication, model);
        return "support";
    }
}
