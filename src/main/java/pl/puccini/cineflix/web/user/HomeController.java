package pl.puccini.cineflix.web.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

@Controller
class HomeController {

    private final MovieService movieService;
    private final SeriesService seriesService;
    private final UserUtils userUtils;
    private final HomeService homeService;

    HomeController(MovieService movieService, SeriesService seriesService, UserUtils userUtils, HomeService homeService) {
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.userUtils = userUtils;
        this.homeService = homeService;
    }
    @GetMapping("/")
    String home(Authentication authentication, Model model) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        userUtils.addAvatarUrlToModel(authentication, model);
        model.addAttribute("randomPromotedItems", homeService.getRandomPromotedItem(userId));

        model.addAttribute("seriesPromoBoxMainTitle", "Engaging hospital series");
        model.addAttribute("seriesPromoBox", homeService.getSeriesPromoBox(userId));

        model.addAttribute("moviePromoBoxMainTitle", "Trending movies");
        model.addAttribute("moviePromoBox", homeService.getMoviePromoBox(userId));

        String dramaGenre = "Drama";
        model.addAttribute("dramaSeriesTitle", "Drama Series");
        model.addAttribute("dramaSeries", seriesService.getSeriesByGenre(dramaGenre, userId));
        model.addAttribute("dramaGenre", dramaGenre.toLowerCase());

        String comedyGenre = "Comedy";
        model.addAttribute("comedySeriesTitle", "Comedy Series");
        model.addAttribute("comedySeries", seriesService.getSeriesByGenre(comedyGenre, userId));
        model.addAttribute("comedyGenre", comedyGenre.toLowerCase());

        String actionGenre = "Action";
        model.addAttribute("actionSeriesTitle", "Action Series");
        model.addAttribute("actionSeries", seriesService.getSeriesByGenre(actionGenre, userId));
        model.addAttribute("actionGenre", actionGenre.toLowerCase());

        String thrillerGenre = "Thriller";
        model.addAttribute("thrillerMoviesTitle", "Action Movies");
        model.addAttribute("thrillerMovies", movieService.getMovieByGenre(thrillerGenre, userId));
        model.addAttribute("thrillerGenre", thrillerGenre.toLowerCase());

        return "index";
    }
}
