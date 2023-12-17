package pl.puccini.cineflix.domain.user.userRatings.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.UserUtils;
import pl.puccini.cineflix.domain.user.userRatings.UserRatingFacade;

@Controller
@RequestMapping("/rate")
public class RatingController {

    private final UserUtils userUtils;
    private final MovieFacade movieFacade;
    private final SeriesFacade seriesFacade;
    private final UserRatingFacade userRatingFacade;

    public RatingController(UserUtils userUtils, MovieFacade movieFacade, SeriesFacade seriesFacade, UserRatingFacade userRatingFacade) {
        this.userUtils = userUtils;
        this.movieFacade = movieFacade;
        this.seriesFacade = seriesFacade;
        this.userRatingFacade = userRatingFacade;
    }


    @PostMapping("/{direction}/{imdbId}")
    public ResponseEntity<?> rateItem(@PathVariable String direction, @PathVariable String imdbId, Authentication authentication) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        boolean isUpvote = "up".equals(direction);

        try {
            if (movieFacade.doesMovieExists(imdbId)) {
                userRatingFacade.rateMovie(imdbId, userId, isUpvote);
            } else if (seriesFacade.doesSeriesExists(imdbId)) {
                userRatingFacade.rateSeries(imdbId, userId, isUpvote);
            } else {
                return ResponseEntity.badRequest().body("Invalid IMDb ID.");
            }
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid request: " + ex.getMessage());
        }
    }
}
