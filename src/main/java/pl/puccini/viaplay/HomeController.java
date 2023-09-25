package pl.puccini.viaplay;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.viaplay.domain.movie.service.MovieService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDtoMapper;
import pl.puccini.viaplay.domain.series.service.SeriesService;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;

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
        model.addAttribute("randomPromotedItems", getRandomPromotedItem());
        model.addAttribute("seriesPromoBoxMainTitle", "Wciągające historie ze szpitalnych korytarzy");
        model.addAttribute("seriesPromoBox", getSeriesPromoBox());
        return "index";
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
//            TO DO
            return null;
        }
    }
    private List<SeriesDto> getSeriesPromoBox(){
        List<SeriesDto> promoBoxSeries = new ArrayList<>();
        promoBoxSeries.addAll(seriesService.getSeriesByImdbId("tt7817340"));
        promoBoxSeries.addAll(seriesService.getSeriesByImdbId("tt6470478"));
        promoBoxSeries.addAll(seriesService.getSeriesByImdbId("tt4655480"));
        promoBoxSeries.addAll(seriesService.getSeriesByImdbId("tt6236572"));
        promoBoxSeries.addAll(seriesService.getSeriesByImdbId("tt6664638"));
        return promoBoxSeries;
    }
}
