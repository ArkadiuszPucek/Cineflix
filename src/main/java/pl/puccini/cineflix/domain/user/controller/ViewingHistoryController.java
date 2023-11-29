package pl.puccini.cineflix.domain.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.user.dto.WatchedItemDto;
import pl.puccini.cineflix.domain.user.service.UserService;
import pl.puccini.cineflix.domain.user.service.UserUtils;
import pl.puccini.cineflix.domain.user.service.ViewingHistoryService;

import java.util.List;


@Controller
public class ViewingHistoryController {
    private final UserUtils userUtils;
    private final UserService userService;
    private final EpisodeService episodeService;
    private final MovieService movieService;
    private final ViewingHistoryService viewingHistoryService;

    public ViewingHistoryController(UserUtils userUtils, UserService userService, EpisodeService episodeService, MovieService movieService, ViewingHistoryService viewingHistoryService) {
        this.userUtils = userUtils;
        this.userService = userService;
        this.episodeService = episodeService;
        this.movieService = movieService;
        this.viewingHistoryService = viewingHistoryService;
    }

    @GetMapping("/watched")
    public String getWatchedPage(Model model, Authentication authentication){
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        List<WatchedItemDto> watchedItems = viewingHistoryService.getWatchedItems(userId);

        model.addAttribute("watchedItems", watchedItems);

        return "admin/users/watched";
    }



    @PostMapping("/add-episode-to-history/{episodeId}")
    public String addEpisodeToHistory(@PathVariable Long episodeId, Authentication authentication, RedirectAttributes redirectAttributes) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        viewingHistoryService.saveEpisodeToViewingHistory(userId, episodeId);

        redirectAttributes.addAttribute("episodeId", episodeId);
        return "redirect:/play-episode/{episodeId}";
    }

    @PostMapping("/add-movie-to-history/{imdbId}")
    public String addMovieToHistory(@PathVariable String imdbId, Authentication authentication, RedirectAttributes redirectAttributes) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        viewingHistoryService.saveMovieToViewingHistory(userId, imdbId);
        redirectAttributes.addFlashAttribute("imdbId", imdbId);

        return "redirect:/movies/play-movie/" + imdbId;
    }

}
