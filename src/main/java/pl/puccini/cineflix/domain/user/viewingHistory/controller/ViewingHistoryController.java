package pl.puccini.cineflix.domain.user.viewingHistory.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.user.viewingHistory.dto.WatchedItemDto;
import pl.puccini.cineflix.domain.UserUtils;
import pl.puccini.cineflix.domain.user.viewingHistory.ViewingHistoryFacade;

import java.util.List;


@Controller
public class ViewingHistoryController {
    private final UserUtils userUtils;
    private final ViewingHistoryFacade viewingHistoryFacade;

    public ViewingHistoryController(UserUtils userUtils, ViewingHistoryFacade viewingHistoryFacade) {
        this.userUtils = userUtils;
        this.viewingHistoryFacade = viewingHistoryFacade;
    }


    @GetMapping("/watched")
    public String getWatchedPage(Model model, Authentication authentication){
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        List<WatchedItemDto> watchedItems = viewingHistoryFacade.getWatchedItems(userId);

        model.addAttribute("watchedItems", watchedItems);

        return "admin/users/watched";
    }

    @PostMapping("/add-episode-to-history/{episodeId}")
    public String addEpisodeToHistory(@PathVariable Long episodeId, Authentication authentication, RedirectAttributes redirectAttributes) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        viewingHistoryFacade.saveEpisodeToViewingHistory(userId, episodeId);

        redirectAttributes.addAttribute("episodeId", episodeId);
        return "redirect:/play-episode/{episodeId}";
    }

    @PostMapping("/add-movie-to-history/{imdbId}")
    public String addMovieToHistory(@PathVariable String imdbId, Authentication authentication, RedirectAttributes redirectAttributes) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        viewingHistoryFacade.saveMovieToViewingHistory(userId, imdbId);
        redirectAttributes.addFlashAttribute("imdbId", imdbId);

        return "redirect:/movies/play-movie/" + imdbId;
    }

}
