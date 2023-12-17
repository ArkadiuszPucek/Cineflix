package pl.puccini.cineflix.web.admin.usersManagement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.user.userDetails.model.User;

import java.util.List;

@Controller
public class UserAdminController {

    private final UserManagementFacade userManagementFacade;

    public UserAdminController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }


    @GetMapping("/master/manage-users")
    public String showManageUsersForm(Authentication authentication, Model model) {
        userManagementFacade.addAvatarUrlToModel(authentication, model);
        List<User> allUsersInService = userManagementFacade.getAllUsersInService();
        model.addAttribute("users", allUsersInService);

        return "admin/users/manage-users";
    }

    @PostMapping("/master/change-role")
    public String changeUserRole(@RequestParam Long user, @RequestParam String newRole, RedirectAttributes redirectAttributes) {
        try {
            userManagementFacade.changeUserRole(user, newRole);
            redirectAttributes.addFlashAttribute("message", "The user role has been changed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occured: " + e.getMessage());
        }
        return "redirect:/master/manage-users";
    }

    @GetMapping("/master/delete-user/{userId}")
    public String deleteUser(@PathVariable Long userId,
                             RedirectAttributes redirectAttributes,
                             @RequestParam String action,
                             Authentication authentication,
                             Model model) {
        userManagementFacade.addAvatarUrlToModel(authentication, model);
        if ("deleteUser".equals(action)) {
            userManagementFacade.deleteUser(userId);
        } else {
            redirectAttributes.addFlashAttribute("message", "An error occurred while deleting the user.");
        }
        return "redirect:/master/manage-users";
    }


}
