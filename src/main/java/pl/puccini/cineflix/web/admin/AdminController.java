package pl.puccini.cineflix.web.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.cineflix.domain.exceptions.SeriesAlreadyExistsException;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.model.Episode;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.User;
import pl.puccini.cineflix.domain.user.UserDto;
import pl.puccini.cineflix.domain.user.UserService;

import java.io.IOException;
import java.util.*;


@Controller
public class AdminController {
    private final MovieService movieService;
    private final SeriesService seriesService;
    private final EpisodeService episodeService;
    private final GenreService genreService;

    private final UserService userService;

    public AdminController(MovieService movieService, SeriesService seriesService, EpisodeService episodeService, GenreService genreService, UserService userService) {
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.episodeService = episodeService;
        this.genreService = genreService;
        this.userService = userService;
    }


    @GetMapping("/admin")
    public String getAdminPanel() {

        return "admin/admin";
    }

    @PostMapping("/admin/add-movie-form")
    public String addMovieManual(MovieDto movie, RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
        if (movieService.existsByImdbId(movie.getImdbId())) {
            redirectAttributes.addFlashAttribute("error", "Film o podanym IMDb id istnieje w serwisie!");
            return "redirect:/add-movie-form";
        }
        movieService.addMovieManual(movie);
        String normalizedTitle = movie.getTitle().toLowerCase().replace(" ", "-");

        return "redirect:/movies/" + normalizedTitle;
    }

    @GetMapping("/admin/add-movie-form")
    public String showAddMovieManualForm(Model model) {
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/movies/add-movie-form";
    }

    @PostMapping("/admin/add-movie-api")
    public String addMovieByApi(MovieDto movie, RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
        if (movieService.existsByImdbId(movie.getImdbId())) {
            redirectAttributes.addFlashAttribute("error", "Film o podanym IMDb id istnieje w serwisie!");
            return "redirect:/add-movie-api";
        }
        Movie movieFromApi = movieService.addMovieByApi(movie);
        String normalizedTitle = movieFromApi.getTitle().toLowerCase().replace(" ", "-");

        return "redirect:/movies/" + normalizedTitle;
    }

    @GetMapping("/admin/add-movie-api")
    public String showAddMovieApiForm(Model model) {
        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/movies/add-movie-api";
    }


    @GetMapping("/admin/edit-movie/{imdbId}")
    public String showEditMovieForm(@PathVariable String imdbId, Model model) {
        MovieDto movieByImdbId = movieService.findMovieByImdbId(imdbId);

        model.addAttribute("movie", movieByImdbId);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);
        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        return "admin/movies/edit-movie-form";
    }

    @PostMapping("/admin/update-movie")
    public String updateMovie(@ModelAttribute("movie") MovieDto movieDto, RedirectAttributes redirectAttributes) {
        try {
            boolean updateResult = movieService.updateMovie(movieDto);

            if (updateResult) {
                redirectAttributes.addFlashAttribute("success", "Film został pomyślnie zaktualizowany.");
                return "redirect:/admin/manage-movies";
            } else {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono filmu do aktualizacji.");
                return "redirect:/admin/edit-movie/" + movieDto.getImdbId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas aktualizacji filmu: " + e.getMessage());
            return "redirect:/admin/edit-movie/" + movieDto.getImdbId();
        }
    }

    @GetMapping("/admin/delete-movie/{imdbId}")
    public String deleteMovie(@PathVariable String imdbId,
                              RedirectAttributes redirectAttributes,
                              @RequestParam String action) {
        if ("deleteMovie".equals(action)) {
            boolean deleted = movieService.deleteMovieByImdbId(imdbId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Film został pomyślnie usunięty.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono filmu do usunięcia.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas usuwania filmu");
        }
        return "redirect:/admin/manage-movies";
    }

    @GetMapping("/admin/manage-movies")
    public String showManageMovieForm(Model model) {
        List<MovieDto> allMoviesInService = movieService.findAllMoviesInService();
        model.addAttribute("allMoviesInService", allMoviesInService);

        return "admin/movies/manage-movies";
    }


//SERIES


    @PostMapping("/admin/add-series-form")
    public String addSeriesManual(@ModelAttribute SeriesDto series, RedirectAttributes redirectAttributes, HttpSession session) {
        if (seriesService.existsByImdbId(series.getImdbId())) {
            redirectAttributes.addFlashAttribute("message", "Film o podanym IMDb id istnieje w serwisie!");
            return "redirect:/admin/add-series-form";
        }

        try {
            seriesService.addSeriesManual(series);
            session.setAttribute("seasonsCount", series.getSeasonsCount());
            session.setAttribute("title", series.getTitle());
            redirectAttributes.addFlashAttribute("seasonsCount", series.getSeasonsCount());
            redirectAttributes.addFlashAttribute("message", "Serial został dodany.");
            return "redirect:/admin/add-episode/"+series.getImdbId()+"/1/1";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "Błąd podczas dodawania serialu: " + e.getMessage());
            return "redirect:/admin/add-series-form";
        }
    }
    @GetMapping("/admin/add-series-form")
    public String showAddSeriesManualForm(Model model) {
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        SeriesDto series = new SeriesDto();
        model.addAttribute("series", series);

        return "admin/series/add-series-form";
    }

    @GetMapping("/admin/add-episode/{seriesId}/{seasonNumber}/{episodeNumber}")
    public String showAddEpisodeForm(@PathVariable String seriesId,
                                     @PathVariable int seasonNumber,
                                     @PathVariable int episodeNumber,
                                     Model model) {
        EpisodeDto episodeDto = new EpisodeDto();
        model.addAttribute("episode", episodeDto);
        model.addAttribute("seriesId", seriesId);
        model.addAttribute("seasonNumber", seasonNumber);
        model.addAttribute("episodeNumber", episodeNumber);
        return "admin/series/add-episode-form";
    }

    @PostMapping("/admin/add-episode/{seriesId}/{seasonNumber}/{episodeNumber}")
    public String addEpisode(@ModelAttribute EpisodeDto episodeDto,
                             @PathVariable String seriesId,
                             @PathVariable int seasonNumber,
                             @PathVariable int episodeNumber,
                             HttpSession session,
                             @RequestParam String action,
                             RedirectAttributes redirectAttributes) {
        try {
            Integer seasonsCount = (Integer) session.getAttribute("seasonsCount");
            String seriesTitle = (String) session.getAttribute("title");

            if (seasonsCount == null) {
                redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas dodawnia liczby sezonów");
                return "redirect:/admin";
            }

            episodeDto.setEpisodeNumber(episodeNumber);
            String redirectUrl = episodeService.processEpisodeAddition(episodeDto, seriesId, seasonNumber, episodeNumber, seasonsCount, action, seriesTitle);
            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd: " + e.getMessage());
            return "redirect:/admin/add-episode/" + seriesId + "/" + seasonNumber + "/" + episodeNumber;
        }
    }


    @PostMapping("/admin/add-series-api")
    public String addSeriesByApi(SeriesDto series, RedirectAttributes redirectAttributes) {
        try {
            Series seriesFromApi = seriesService.addSeriesIfNotExist(series);
            String normalizedTitle = seriesService.getNormalizedSeriesTitle(seriesFromApi.getTitle());

            return "redirect:/series/" + normalizedTitle +"/sezon-1";
        } catch (SeriesAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/add-series-api";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd: " + e.getMessage());
            return "redirect:/admin/add-series-api";
        }
    }

    @GetMapping("/admin/add-series-api")
    public String showAddSeriesApiForm(Model model) {
        SeriesDto seriesDto = new SeriesDto();
        model.addAttribute("series", seriesDto);

        return "admin/series/add-series-api";
    }

    @GetMapping("/admin/manage-series")
    public String showManageSeriesForm(Model model) {
        List<SeriesDto> allSeriesInService = seriesService.findAllMoviesInService();
        model.addAttribute("allSeriesInService", allSeriesInService);

        return "admin/series/manage-series";
    }

    @GetMapping("/admin/edit-series/{imdbId}")
    public String showEditSeriesForm(@PathVariable String imdbId, Model model) {
        SeriesDto seriesByImdbId = seriesService.findSeriesByImdbId(imdbId);

        model.addAttribute("series", seriesByImdbId);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);
        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        return "admin/series/edit-series-form";
    }

    @PostMapping("/admin/update-series")
    public String updateSeries(@ModelAttribute("series") SeriesDto seriesDto, RedirectAttributes redirectAttributes) {
        try {
            seriesService.updateSeries(seriesDto);
            redirectAttributes.addFlashAttribute("success", "Serial został pomyślnie zaktualizowany.");
            return "redirect:/admin/manage-series";
        } catch (SeriesNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/manage-series";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas aktualizacji serialu: " + e.getMessage());
            return "redirect:/admin/edit-series/" + seriesDto.getImdbId();
        }
    }

    @GetMapping("/admin/delete-series/{imdbId}")
    public String deleteSeries(@PathVariable String imdbId,
                               RedirectAttributes redirectAttributes,
                               @RequestParam String action) {

        if ("deleteSeries".equals(action)) {
            boolean deleted = seriesService.deleteSeriesByImdbId(imdbId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Serial został pomyślnie usunięty.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono serialu do usunięcia.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas usuwania serialu: ");
        }
        return "redirect:/admin/manage-series";
    }

    @GetMapping("/admin/edit-series-seasons/{imdbId}")
    public String showManageSeasonsForm(@PathVariable String imdbId, Model model, HttpSession session) {
        Series series = seriesService.findSeriesByImdbIdSeriesType(imdbId);
        session.setAttribute("imdbId", imdbId);

        if (series == null){
            return "redirect:/admin/manage-series";
        }

        model.addAttribute("series", series);
        model.addAttribute("seasons", series.getSeasons());
        return "admin/series/manage-seasons";
    }

    @GetMapping("/admin/edit-episode/{episodeId}")
    public String showEpisodeEditForm(@PathVariable Long episodeId, Model model) {
        EpisodeDto episodeById = episodeService.getEpisodeById(episodeId);
        model.addAttribute("episode", episodeById);
        return "admin/series/edit-episode-form";
    }

    @PostMapping("/admin/update-episode")
    public String updateSeries(@ModelAttribute("episode") EpisodeDto episodeDto,
                               RedirectAttributes redirectAttributes) {


        try {
            Episode updatedEpisode = episodeService.updateEpisode(episodeDto);
            String seriesImdbId = updatedEpisode.getSeason().getSeries().getImdbId();
            redirectAttributes.addFlashAttribute("message", "Epizod został pomyślnie zaktualizowany.");
            return "redirect:/admin/edit-series-seasons/" + seriesImdbId;
        } catch (EpisodeNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas edytowania epizodu " + e.getMessage());
            return "redirect:/admin/manage-series";
        }
    }

    @GetMapping("/admin/delete-episode/{episodeId}")
    public String deleteEpisode(@PathVariable Long episodeId,
                               RedirectAttributes redirectAttributes,
                               @RequestParam String action) {

        try{
            if ("deleteEpisode".equals(action)){
                Episode episodeToDelete = episodeService.deleteEpisodeById(episodeId);
                String imdbId = episodeToDelete.getSeason().getSeries().getImdbId();
                redirectAttributes.addFlashAttribute("message", "Epizod został pomyślnie usunięty.");
                return "redirect:/admin/edit-series-seasons/" + imdbId;
            }else {
                redirectAttributes.addFlashAttribute("message", "Epizod nie został usunięty.");
                return "redirect:/admin/manage-series";
            }
        } catch (EpisodeNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas edytowania epizodu " + e.getMessage());
            return "redirect:/admin/manage-series";
        }
    }




    @GetMapping("/master/manage-users")
    public String showManageUsersForm(Model model) {
        List<User> allUsersInService = userService.getAllUsersInService();
        model.addAttribute("users", allUsersInService);


        return "admin/users/manage-users";
    }

    @PostMapping("/master/change-role")
    public String changeUserRole(@RequestParam Long user, @RequestParam String newRole, RedirectAttributes redirectAttributes) {
        try {
            userService.changeUserRole(user, newRole);
            redirectAttributes.addFlashAttribute("message", "Rola użytkownika została zmieniona.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd: " + e.getMessage());
        }
        return "redirect:/master/manage-users";
    }


    @GetMapping("/master/edit-user/{userId}")
    public String showEditUserForm(@PathVariable String userId, Model model) {
//        MovieDto movieByImdbId = movieService.findMovieByImdbId(imdbId);
//
//        model.addAttribute("movie", movieByImdbId);
//        List<Genre> allGenres = genreService.getAllGenres();
//        model.addAttribute("genres", allGenres);
//        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
//        model.addAttribute("ageLimits", ageLimits);
//
//        return "admin/movies/edit-movie-form";
        return null;
    }

    @PostMapping("/master/update-user")
    public String updateUser(@ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
//        try {
//            boolean updateResult = movieService.updateMovie(movieDto);
//
//            if (updateResult) {
//                redirectAttributes.addFlashAttribute("success", "Film został pomyślnie zaktualizowany.");
//                return "redirect:/admin/manage-movies";
//            } else {
//                redirectAttributes.addFlashAttribute("error", "Nie znaleziono filmu do aktualizacji.");
//                return "redirect:/admin/edit-movie/" + movieDto.getImdbId();
//            }
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas aktualizacji filmu: " + e.getMessage());
//            return "redirect:/admin/edit-movie/" + movieDto.getImdbId();
        return null;
//        }
    }


    @GetMapping("/master/delete-user/{userId}")
    public String deleteUser(@PathVariable Long userId,
                              RedirectAttributes redirectAttributes,
                              @RequestParam String action) {
        if ("deleteUser".equals(action)) {
            boolean deleted = userService.deleteUserById(userId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("message", "Film został pomyślnie usunięty.");
            } else {
                redirectAttributes.addFlashAttribute("message", "Nie znaleziono filmu do usunięcia.");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas usuwania filmu");
        }
        return "redirect:/master/manage-users";
    }



}
