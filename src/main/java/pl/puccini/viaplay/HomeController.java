package pl.puccini.viaplay;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.viaplay.domain.movie.MovieService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;

import java.util.List;

@Controller
class HomeController {

    private final MovieService movieService;

    HomeController(MovieService movieService) {
        this.movieService = movieService;
    }


    @GetMapping("/")
    String home(Model model){
//        List<MovieDto> promotedMovies = movieService.findAllPromotedMovies();
//        model.addAttribute("media", promotedMovies);
        return "movie-listing";
    }
}
