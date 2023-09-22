package pl.puccini.viaplay;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.viaplay.domain.movie.MovieService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.series.SeriesService;
import pl.puccini.viaplay.domain.series.SeriesDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Controller
class HomeController {

    private final MovieService movieService;
    private final SeriesService seriesService;

    HomeController(MovieService movieService, SeriesService seriesService) {
        this.movieService = movieService;
        this.seriesService = seriesService;
    }

    @GetMapping("/")
    String home(Model model){
        Object randomPromotedItem = getRandomPromotedItem();
        model.addAttribute("randomPromotedItems", randomPromotedItem);
        return "movie-listing";
    }

    private Object getRandomPromotedItem(){
        List<MovieDto> allPromotedMovies = movieService.findAllPromotedMovies();
        List<SeriesDto> allPromotedSeries = seriesService.findAllPromotedMovies();

        List<Object> promotedItems = new ArrayList<>();
        promotedItems.addAll(allPromotedMovies);
        promotedItems.addAll(allPromotedSeries);

        int size = promotedItems.size();

        if (size > 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(size);
            return promotedItems.get(randomIndex);
        }else {
            return movieService.getFirstMovie();
        }
    }
}
