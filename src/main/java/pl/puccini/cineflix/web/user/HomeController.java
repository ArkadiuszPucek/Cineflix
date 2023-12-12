package pl.puccini.cineflix.web.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.config.carousel.movies.MoviesCarouselConfigDto;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.config.carousel.series.SeriesCarouselConfigDto;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.List;

@Controller
class HomeController {

    private final MovieService movieService;
    private final SeriesService seriesService;
    private final UserUtils userUtils;
    private final HomeService homeService;
    private final GenreService genreService;

    HomeController(MovieService movieService, SeriesService seriesService, UserUtils userUtils, HomeService homeService, GenreService genreService) {
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.userUtils = userUtils;
        this.homeService = homeService;
        this.genreService = genreService;
    }
    @GetMapping("/")
    String home(Authentication authentication, Model model) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        userUtils.addAvatarUrlToModel(authentication, model);

        model.addAttribute("randomPromotedItems", homeService.getRandomPromotedItem(userId));

        model.addAttribute("seriesPromoBoxMainTitle", seriesService.getSeriesPromoBoxTitle());
        model.addAttribute("seriesPromoBox", seriesService.getSeriesPromoBox(userId));

        model.addAttribute("moviePromoBoxMainTitle", movieService.getMoviesPromoBoxTitle());
        model.addAttribute("moviePromoBox", movieService.getMoviePromoBox(userId));

        List<SeriesCarouselConfigDto> seriesCarousels = homeService.getSeriesCarouselsByActiveGenres(userId);
        model.addAttribute("seriesCarousels", seriesCarousels);

        List<MoviesCarouselConfigDto> moviesCarousels = homeService.getMoviesCarouselsByActiveGenres(userId);
        model.addAttribute("moviesCarousels", moviesCarousels);

//        String thrillerGenre = "Thriller";
//        model.addAttribute("thrillerMoviesTitle", "Action Movies");
//        model.addAttribute("thrillerMovies", movieService.getMovieByGenre(thrillerGenre, userId));
//        model.addAttribute("thrillerGenre", thrillerGenre.toLowerCase());

        return "index";
    }

}
