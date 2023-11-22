package pl.puccini.cineflix.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.exceptions.UsernameExistsException;
import pl.puccini.cineflix.domain.user.UserDto;
import pl.puccini.cineflix.domain.user.UserService;

@Controller
public class RegisterController {
    private UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    private String showRegistrationForm(Model model){
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "register-form";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserDto userDto,
                                      RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUserAccount(userDto);
            redirectAttributes.addFlashAttribute("message", "Rejestracja przebiegła pomyślnie.");
            return "redirect:/login";
        } catch (UsernameExistsException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/register";
        }
    }


}
