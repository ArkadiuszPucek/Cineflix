package pl.puccini.cineflix.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.user.service.RatingService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

@Controller
@RequestMapping("/rate")
public class RatingController {

    private final RatingService ratingService;
    private final UserUtils userUtils;

    @Autowired
    public RatingController(RatingService ratingService, UserUtils userUtils) {
        this.ratingService = ratingService;
        this.userUtils = userUtils;
    }

    @PostMapping("/{direction}/{imdbId}")
    public ResponseEntity<?> rateItem(@PathVariable String direction, @PathVariable String imdbId, Authentication authentication) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        boolean isUpvote = "up".equals(direction);

        try {
            ratingService.rateItem(imdbId, userId, isUpvote);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono użytkownika.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Nieprawidłowe żądanie: " + ex.getMessage());
        }
    }
}
