package pl.puccini.viaplay;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.puccini.viaplay.domain.kids.KidsContentService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.service.MovieService;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.service.SeriesService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class SearchController {
    private final MovieService movieService;
    private final SeriesService seriesService;
    private final KidsContentService kidsContentService;


    public SearchController(MovieService movieService, SeriesService seriesService, KidsContentService kidsContentService) {
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.kidsContentService = kidsContentService;
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "filter", defaultValue = "all") String filter,
            Model model) {

        if(query != null && !query.isEmpty()) {
            String loweredQuery = query.toLowerCase();

            List<MovieDto> movies = movieService.searchMovies(loweredQuery);
            List<SeriesDto> series = seriesService.searchSeries(loweredQuery);

            if ("movies".equals(filter)) {
                model.addAttribute("activeAttribute", movies);
            } else if ("series".equals(filter)) {
                model.addAttribute("activeAttribute", series);
            } else if ("kids".equals(filter)) {
                List<Movie> allKidsMovies = kidsContentService.getAllKidsMovies();
                List<Series> allKidsSeries = kidsContentService.getAllKidsSeries();
                List<Object> mixedKidsContent = new ArrayList<>();
                mixedKidsContent.addAll(allKidsMovies);
                mixedKidsContent.addAll(allKidsSeries);
                Collections.shuffle(mixedKidsContent);
                model.addAttribute("activeAttribute", mixedKidsContent);
            } else {
                List<Object> mixedResults = new ArrayList<>();
                mixedResults.addAll(movies);
                mixedResults.addAll(series);
                Collections.shuffle(mixedResults);
                model.addAttribute("activeAttribute", mixedResults);
            }
        }
        model.addAttribute("searchQuery", query);
        model.addAttribute("activeFilter", filter);
        return "search";
    }


}
