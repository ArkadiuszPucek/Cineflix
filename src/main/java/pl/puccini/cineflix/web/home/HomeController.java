package pl.puccini.cineflix.web.home;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.cineflix.config.carousel.movies.dto.MoviesCarouselConfigDto;
import pl.puccini.cineflix.config.promoBox.PromotionItemFacade;
import pl.puccini.cineflix.config.carousel.series.dto.SeriesCarouselConfigDto;
import pl.puccini.cineflix.domain.UserUtils;

import java.util.List;

@Controller
class HomeController {
    private final UserUtils userUtils;
    private final HomeService homeService;
    private final PromotionItemFacade promotionItemFacade;

    HomeController(UserUtils userUtils, HomeService homeService, PromotionItemFacade promotionItemFacade) {
        this.userUtils = userUtils;
        this.homeService = homeService;
        this.promotionItemFacade = promotionItemFacade;
    }


    @GetMapping("/")
    String home(Authentication authentication, HttpServletRequest request, HttpServletResponse response, Model model) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        if (userId == null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/login";
        }

        userUtils.addAvatarUrlToModel(authentication, model);

        model.addAttribute("randomPromotedItems", homeService.getRandomPromotedItem(userId));

        model.addAttribute("seriesPromoBoxMainTitle", promotionItemFacade.getSeriesPromoBoxTitle());
        model.addAttribute("seriesPromoBox", promotionItemFacade.getSeriesPromoBox(userId));

        model.addAttribute("moviePromoBoxMainTitle", promotionItemFacade.getMoviesPromoBoxTitle());
        model.addAttribute("moviePromoBox", promotionItemFacade.getMoviePromoBox(userId));

        List<SeriesCarouselConfigDto> seriesCarousels = homeService.getSeriesCarouselsByActiveGenres(userId);
        model.addAttribute("seriesCarousels", seriesCarousels);

        List<MoviesCarouselConfigDto> moviesCarousels = homeService.getMoviesCarouselsByActiveGenres(userId);
        model.addAttribute("moviesCarousels", moviesCarousels);

        return "index";
    }

}
