package pl.puccini.cineflix.config.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.puccini.cineflix.domain.user.service.UserUtils;

@Controller
public class CustomErrorController implements ErrorController {
    private final UserUtils userUtils;

    public CustomErrorController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        return "error/not-found";
    }

//    public String getErrorPath() {
//        return "/error";
//    }
}
