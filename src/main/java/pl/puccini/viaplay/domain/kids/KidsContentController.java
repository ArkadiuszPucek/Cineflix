package pl.puccini.viaplay.domain.kids;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.model.Series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/kids")
public class KidsContentController {
    private final KidsContentService kidsContentService;

    public KidsContentController(KidsContentService kidsContentService) {
        this.kidsContentService = kidsContentService;
    }

    @GetMapping()
    public String search(
            @RequestParam(name = "filter", defaultValue = "all") String filter,
            Model model) {

        List<Movie> allKidsMovies = kidsContentService.getAllKidsMovies();
        List<Series> allKidsSeries = kidsContentService.getAllKidsSeries();


        if ("movies".equals(filter)) {
            model.addAttribute("activeAttribute", allKidsMovies);
        } else if ("series".equals(filter)) {
            model.addAttribute("activeAttribute", allKidsSeries);
        } else {
            List<Object> mixedResults = new ArrayList<>();
            mixedResults.addAll(allKidsMovies);
            mixedResults.addAll(allKidsSeries);
            Collections.shuffle(mixedResults);
            model.addAttribute("activeAttribute", mixedResults);
        }
        model.addAttribute("activeFilter",filter);
        return"kids";
    }
}

