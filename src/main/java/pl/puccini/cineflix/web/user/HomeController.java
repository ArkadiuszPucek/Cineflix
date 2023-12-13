package pl.puccini.cineflix.web.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.cineflix.config.carousel.movies.dto.MoviesCarouselConfigDto;
import pl.puccini.cineflix.domain.movie.service.MoviePromotionService;
import pl.puccini.cineflix.config.carousel.series.dto.SeriesCarouselConfigDto;
import pl.puccini.cineflix.domain.series.service.SeriesPromotionService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.List;

@Controller
class HomeController {
    private final UserUtils userUtils;
    private final HomeService homeService;
    private final MoviePromotionService moviePromotionService;
    private final SeriesPromotionService seriesPromotionService;

    HomeController(UserUtils userUtils, HomeService homeService, MoviePromotionService moviePromotionService, SeriesPromotionService seriesPromotionService) {
        this.userUtils = userUtils;
        this.homeService = homeService;
        this.moviePromotionService = moviePromotionService;
        this.seriesPromotionService = seriesPromotionService;
    }


    @GetMapping("/")
    String home(Authentication authentication, Model model) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        userUtils.addAvatarUrlToModel(authentication, model);

        model.addAttribute("randomPromotedItems", homeService.getRandomPromotedItem(userId));

        model.addAttribute("seriesPromoBoxMainTitle", seriesPromotionService.getSeriesPromoBoxTitle());
        model.addAttribute("seriesPromoBox", seriesPromotionService.getSeriesPromoBox(userId));

        model.addAttribute("moviePromoBoxMainTitle", moviePromotionService.getMoviesPromoBoxTitle());
        model.addAttribute("moviePromoBox", moviePromotionService.getMoviePromoBox(userId));

        List<SeriesCarouselConfigDto> seriesCarousels = homeService.getSeriesCarouselsByActiveGenres(userId);
        model.addAttribute("seriesCarousels", seriesCarousels);

        List<MoviesCarouselConfigDto> moviesCarousels = homeService.getMoviesCarouselsByActiveGenres(userId);
        model.addAttribute("moviesCarousels", moviesCarousels);

        return "index";
    }

}
