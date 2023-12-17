package pl.puccini.cineflix.domain.user.userLists.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.UserUtils;
import pl.puccini.cineflix.domain.user.userLists.UserListFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/library")
public class UserListController {
    private final UserUtils userUtils;
    private final UserListFacade userListFacade;

    public UserListController(UserUtils userUtils, UserListFacade userListFacade) {
        this.userUtils = userUtils;
        this.userListFacade = userListFacade;
    }


    @GetMapping
    public String viewList(@RequestParam(name = "filter", defaultValue = "all") String filter,
                           Authentication authentication,
                           Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        List<MovieDto> userMovies = userListFacade.getUserMovies(userId);
        List<SeriesDto> userSeries = userListFacade.getUserSeries(userId);

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
        model.addAttribute("activeFilter", filter);

        return "/library";
    }

    @PostMapping("/add-to-list/{imdbId}")
    @ResponseBody
    public ResponseEntity<?> addItemToList(@PathVariable String imdbId, Authentication authentication) {
        try {
            Long userId = userUtils.getUserIdFromAuthentication(authentication);
            userListFacade.addItemToList(userId, imdbId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error message");
        }
    }

    @DeleteMapping("/remove-from-list/{imdbId}")
    public ResponseEntity<?> removeItemFromList(@PathVariable String imdbId, Authentication authentication) {
        try {
            Long userId = userUtils.getUserIdFromAuthentication(authentication);
            userListFacade.removeItemFromList(userId, imdbId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error message");
        }
    }
}
