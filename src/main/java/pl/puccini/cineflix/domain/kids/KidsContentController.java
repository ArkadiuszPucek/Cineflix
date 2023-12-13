package pl.puccini.cineflix.domain.kids;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/kids")
public class KidsContentController {
    private final KidsSeriesService kidsSeriesService;
    private final KidsMovieService kidsMovieService;
    private final UserUtils userUtils;

    public KidsContentController(KidsSeriesService kidsSeriesService, KidsMovieService kidsMovieService, UserUtils userUtils) {
        this.kidsSeriesService = kidsSeriesService;
        this.kidsMovieService = kidsMovieService;
        this.userUtils = userUtils;
    }

    @GetMapping()
    public String search(@RequestParam(name = "filter", defaultValue = "all") String filter, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        model.addAttribute("activeAttribute", getFilteredContent(filter, userId));
        model.addAttribute("activeFilter", filter);

        return "kids";
    }

    private List<Object> getFilteredContent(String filter, Long userId) {
        if ("movies".equals(filter)) {
            return new ArrayList<>(kidsMovieService.getAllKidsMovies(userId));
        } else if ("series".equals(filter)) {
            return new ArrayList<>(kidsSeriesService.getAllKidsSeries(userId));
        } else {
            return mixResults(kidsMovieService.getAllKidsMovies(userId), kidsSeriesService.getAllKidsSeries(userId));
        }
    }

    private List<Object> mixResults(List<MovieDto> movies, List<SeriesDto> series) {
        List<Object> mixedResults = new ArrayList<>();
        mixedResults.addAll(movies);
        mixedResults.addAll(series);
        Collections.shuffle(mixedResults);
        return mixedResults;
    }
}

