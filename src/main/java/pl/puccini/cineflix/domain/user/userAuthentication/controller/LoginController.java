package pl.puccini.cineflix.domain.user.userAuthentication.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.RequestContextUtils;
//import pl.puccini.cineflix.domain.user.userAuthentication.service.CustomUserDetailsService;

import java.util.Map;

@Controller
public class LoginController {

    @GetMapping("/login")
    private String loginForm(Model model, HttpServletRequest request){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }

        if (request.getParameter("error") != null) {
            model.addAttribute("loginError", "Incorrect email address or password.");
        }

        Map<String, ?> flashAttributes = RequestContextUtils.getInputFlashMap(request);
        if (flashAttributes != null) {
            model.addAllAttributes(flashAttributes);
        }

        return "login-form";
    }
}
