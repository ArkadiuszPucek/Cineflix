package pl.puccini.cineflix.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.service.UserListService;
import pl.puccini.cineflix.domain.user.service.UserService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/library")
public class UserListController {
    private final UserListService userListService;
    private final UserService userService;
    private final UserUtils userUtils;

    @Autowired
    public UserListController(UserListService userListService, UserService userService, UserUtils userUtils) {
        this.userListService = userListService;
        this.userService = userService;
        this.userUtils = userUtils;
    }

    @GetMapping
    public String viewList(@RequestParam(name = "filter", defaultValue = "all") String filter,
                           Authentication authentication,
                           Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = getUserIdFromAuthentication(authentication);

        List<Movie> userMovies = userListService.getUserMovies(userId);
        List<Series> userSeries = userListService.getUserSeries(userId);
        if ("movies".equals(filter)) {
            model.addAttribute("activeAttribute", userMovies);
        } else if ("series".equals(filter)) {
            model.addAttribute("activeAttribute", userSeries);
        } else {
            List<Object> mixedResults = new ArrayList<>();
            mixedResults.addAll(userMovies);
            mixedResults.addAll(userSeries);
            Collections.shuffle(mixedResults);
            model.addAttribute("activeAttribute", mixedResults);
        }
        model.addAttribute("activeFilter",filter);

        return "/admin/users/library";
    }

    @PostMapping("/add-to-list/{imdbId}")
    @ResponseBody
    public ResponseEntity<?> addItemToList(@PathVariable String imdbId, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            userListService.addItemToList(userId, imdbId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error message");
        }
    }

    @PostMapping("/remove")
    public String removeItemFromList(@RequestParam String imdbId, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        userListService.removeItemFromList(userId, imdbId);
        return "redirect:/user-list";
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userService.findByUsername(email);
            return user.getId();
        }else {
            return null;
        }
    }
}
