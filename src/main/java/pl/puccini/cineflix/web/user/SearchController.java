package pl.puccini.cineflix.web.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.puccini.cineflix.domain.kids.KidsContentService;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class SearchController {
    private final MovieService movieService;
    private final SeriesService seriesService;
    private final KidsContentService kidsContentService;
    private final UserUtils userUtils;


    public SearchController(MovieService movieService, SeriesService seriesService, KidsContentService kidsContentService, UserUtils userUtils) {
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.kidsContentService = kidsContentService;
        this.userUtils = userUtils;
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "filter", defaultValue = "all") String filter,
            Model model, Authentication authentication) {

        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);


        if(query != null && !query.isEmpty()) {
            String loweredQuery = query.toLowerCase();

            List<MovieDto> movies = movieService.searchMovies(loweredQuery);
            List<SeriesDto> series = seriesService.searchSeries(loweredQuery);

            if ("movies".equals(filter)) {
                model.addAttribute("activeAttribute", movies);
            } else if ("series".equals(filter)) {
                model.addAttribute("activeAttribute", series);
            } else if ("kids".equals(filter)) {
                List<MovieDto> allKidsMovies = kidsContentService.getAllKidsMovies(userId);
                List<SeriesDto> allKidsSeries = kidsContentService.getAllKidsSeries(userId);
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
