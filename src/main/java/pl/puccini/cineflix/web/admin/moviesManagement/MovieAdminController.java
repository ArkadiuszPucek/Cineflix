package pl.puccini.cineflix.web.admin.moviesManagement;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.exceptions.IncorrectTypeException;
import pl.puccini.cineflix.domain.exceptions.MovieAlreadyExistsException;
import pl.puccini.cineflix.domain.exceptions.MovieNotFoundException;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MovieAdminController {
    private final MovieManagementFacade movieManagementFacade;

    public MovieAdminController(MovieManagementFacade movieManagementFacade) {
        this.movieManagementFacade = movieManagementFacade;
    }

    @PostMapping("/admin/add-movie-form")
    public String addMovieManual(MovieDto movie, RedirectAttributes redirectAttributes) {
        if (movieManagementFacade.doesMovieExists(movie.getImdbId())) {
            redirectAttributes.addFlashAttribute("error", "A film with the given IMDb id exists on the website!");
            return "redirect:/add-movie-form";
        }
        movieManagementFacade.addMovieManual(movie);
        String normalizedTitle = movieManagementFacade.getNormalizedMovieTitle(movie.getTitle());


        return "redirect:/movies/" + normalizedTitle;
    }

    @GetMapping("/admin/add-movie-form")
    public String showAddMovieManualForm(Authentication authentication , Model model) {
        movieManagementFacade.addAvatarUrlToModel(authentication, model);
        List<Genre> allGenres = movieManagementFacade.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/movies/add-movie-form";
    }

    @PostMapping("/admin/add-movie-api")
    public String addMovieByApi(MovieDto movie, RedirectAttributes redirectAttributes) {
        try {
            Movie movieFromApi = movieManagementFacade.addMovieIfNotExist(movie);
            String normalizedTitle = movieManagementFacade.getNormalizedMovieTitle(movieFromApi.getTitle());
            return "redirect:/movies/" + normalizedTitle;
        } catch (IncorrectTypeException | MovieAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/add-movie-api";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "An error occured: " + e.getMessage());
            return "redirect:/admin/add-movie-api";
        }
    }

    @GetMapping("/admin/add-movie-api")
    public String showAddMovieApiForm(Authentication authentication, Model model) {
        movieManagementFacade.addAvatarUrlToModel(authentication, model);
        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/movies/add-movie-api";
    }


    @GetMapping("/admin/edit-movie/{imdbId}")
    public String showEditMovieForm(@PathVariable String imdbId, Authentication authentication, Model model) {
        movieManagementFacade.addAvatarUrlToModel(authentication, model);
        MovieDto movieByImdbId = movieManagementFacade.getMovieDtoByImdbId(imdbId);

        model.addAttribute("movie", movieByImdbId);
        List<Genre> allGenres = movieManagementFacade.getAllGenres();
        model.addAttribute("genres", allGenres);
        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        return "admin/movies/edit-movie-form";
    }

    @PostMapping("/admin/update-movie")
    public String updateMovie(@ModelAttribute("movie") MovieDto movieDto, RedirectAttributes redirectAttributes) {
        try {
            boolean updateResult = movieManagementFacade.updateMovie(movieDto);

            if (updateResult) {
                redirectAttributes.addFlashAttribute("success", "The video has been successfully updated.");
                return "redirect:/admin/manage-movies";
            } else {
                redirectAttributes.addFlashAttribute("error", "Movie not found");
                return "redirect:/admin/edit-movie/" + movieDto.getImdbId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the video: " + e.getMessage());
            return "redirect:/admin/edit-movie/" + movieDto.getImdbId();
        }
    }

    @GetMapping("/admin/delete-movie/{imdbId}")
    public String deleteMovie(@PathVariable String imdbId,
                              RedirectAttributes redirectAttributes,
                              @RequestParam String action,
                              Authentication authentication,
                              Model model) {
        movieManagementFacade.addAvatarUrlToModel(authentication, model);
        if ("deleteMovie".equals(action)) {
            boolean deleted = movieManagementFacade.deleteMovie(imdbId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "The video has been successfully removed.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Movie not found");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the video.");
        }
        return "redirect:/admin/manage-movies";
    }

    @GetMapping("/admin/manage-movies")
    public String showManageMovieForm(Authentication authentication, Model model) {
        movieManagementFacade.addAvatarUrlToModel(authentication, model);
        Long userId = movieManagementFacade.getUserIdFromAuthentication(authentication);
        List<MovieDto> allMoviesInService = movieManagementFacade.findAllMovies(userId);
        model.addAttribute("allMoviesInService", allMoviesInService);

        return "admin/movies/manage-movies";
    }

    @GetMapping("/admin/manage-movies-promo-box")
    public String moviesPromoBoxForm(Model model, Authentication authentication){
        movieManagementFacade.addAvatarUrlToModel(authentication, model);
        Long userId = movieManagementFacade.getUserIdFromAuthentication(authentication);

        model.addAttribute("currentPromoBoxImdbIds", movieManagementFacade.getMoviePromoBox(userId));
        model.addAttribute("moviePromoBoxTitle", movieManagementFacade.getMoviesPromoBoxTitle());

        return "/admin/movies/movies-promo-box-form";
    }

    @PostMapping("/admin/manage-movies-promo-box/save")
    public String saveMoviesPromoBox(
            @RequestParam String title,
            @RequestParam String imdbId1,
            @RequestParam String imdbId2,
            @RequestParam String imdbId3,
            @RequestParam String imdbId4,
            @RequestParam String imdbId5,
            RedirectAttributes redirectAttributes) {
        try {
            movieManagementFacade.updateMoviePromoBox(title, imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        }catch (MovieNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/admin/manage-movies-promo-box";
        }
        return "redirect:/";
    }

    @GetMapping("/admin/manage-movies-carousels")
    public String manageMoviesCarouselForm(Model model, Authentication authentication){
        movieManagementFacade.addAvatarUrlToModel(authentication, model);
        List<Genre> genres = movieManagementFacade.getGenresWithMinimumMovies(6);
        List<String> activeGenres = movieManagementFacade.getSelectedGenresForMovies();

        Map<String, Boolean> genresWithStatus = genres.stream()
                .collect(Collectors.toMap(Genre::getGenreType, genre -> activeGenres.contains(genre.getGenreType())));

        model.addAttribute("genresWithStatus", genresWithStatus);

        return "admin/movies/manage-movies-carousels-form";
    }

    @PostMapping("/admin/manage-movies-carousels/save")
    public String saveMoviesCarousels(@RequestParam(required = false) List<String> selectedGenres, RedirectAttributes redirectAttributes){
        if (selectedGenres == null) {
            selectedGenres = new ArrayList<>();
        }
        movieManagementFacade.saveSelectedGenresForMovies(selectedGenres);

        redirectAttributes.addFlashAttribute("message", "Carousels settings updated successfully.");
        return "redirect:/admin/manage-movies-carousels";
    }
}
