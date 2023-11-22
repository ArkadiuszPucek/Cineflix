package pl.puccini.cineflix.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.RequestContextUtils;
import pl.puccini.cineflix.domain.exceptions.UsernameExistsException;
//import pl.puccini.cineflix.domain.user.CustomUserDetailsService;

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
            model.addAttribute("loginError", "Niepoprawny adres e-mail lub hasło.");
        }


        Map<String, ?> flashAttributes = RequestContextUtils.getInputFlashMap(request);
        if (flashAttributes != null) {
            model.addAllAttributes(flashAttributes);
        }

        return "login-form";
    }
}
