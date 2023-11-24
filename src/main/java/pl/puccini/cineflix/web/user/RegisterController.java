package pl.puccini.cineflix.web.user;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.exceptions.UsernameExistsException;
import pl.puccini.cineflix.domain.user.dto.UserDto;
import pl.puccini.cineflix.domain.user.service.UserService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

@Controller
public class RegisterController {
    private final UserService userService;
    private final UserUtils userUtils;

    public RegisterController(UserService userService, UserUtils userUtils) {
        this.userService = userService;
        this.userUtils = userUtils;
    }

    @GetMapping("/register")
    private String showRegistrationForm(Model model, Authentication authentication){
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userUtils.addAvatarUrlToModel(authentication, model);

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "register-form";
    }

    @PostMapping("/register")
    public String registerUserAccount(@Valid @ModelAttribute("user") UserDto userDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register-form";
        }
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
