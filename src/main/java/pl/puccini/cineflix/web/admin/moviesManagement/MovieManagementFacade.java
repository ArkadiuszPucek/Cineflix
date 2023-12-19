package pl.puccini.cineflix.web.admin.moviesManagement;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.puccini.cineflix.config.carousel.CarouselFacade;
import pl.puccini.cineflix.config.promoBox.PromotionItemFacade;
import pl.puccini.cineflix.domain.UserUtils;
import pl.puccini.cineflix.domain.genre.GenreFacade;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;

import java.io.IOException;
import java.util.List;

@Service
public class MovieManagementFacade {
    private final MovieFacade movieFacade;
    private final GenreFacade genreFacade;
    private final PromotionItemFacade promotionItemFacade;
    private final CarouselFacade carouselFacade;
    private final UserUtils userUtils;

    public MovieManagementFacade(MovieFacade movieFacade,
                                 GenreFacade genreFacade,
                                 PromotionItemFacade promotionItemFacade,
                                 CarouselFacade carouselFacade, UserUtils userUtils) {
        this.movieFacade = movieFacade;
        this.genreFacade = genreFacade;
        this.promotionItemFacade = promotionItemFacade;
        this.carouselFacade = carouselFacade;
        this.userUtils = userUtils;
    }

    public boolean doesMovieExists(String imdbId) {
        return movieFacade.doesMovieExists(imdbId);
    }

    public void addMovieManual(MovieDto movieDto){
        movieFacade.addMovieManual(movieDto);
    }

    public void addAvatarUrlToModel(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
    }

    public List<Genre> getAllGenres(){
        return genreFacade.getAllGenres();
    }

    public Movie addMovieIfNotExist(MovieDto movieDto) throws IOException, InterruptedException {
        return movieFacade.addMovieIfNotExist(movieDto);
    }

    public MovieDto getMovieDtoByImdbId(String imdbId) {
        return movieFacade.getMovieDtoByImdbId(imdbId);
    }

    public boolean updateMovie(MovieDto movieDto) {
        return movieFacade.updateMovie(movieDto);
    }

    public boolean deleteMovie(String imdbId) {
        return movieFacade.deleteMovie(imdbId);
    }

    public Long getUserIdFromAuthentication(Authentication authentication) {
        return userUtils.getUserIdFromAuthentication(authentication);
    }

    public List<MovieDto> findAllMovies(Long userId){
        return movieFacade.findAllMovies(userId);
    }

    public List<MovieDto> getMoviePromoBox(Long userId) {
        return promotionItemFacade.getMoviePromoBox(userId);
    }

    public String getMoviesPromoBoxTitle() {
        return promotionItemFacade.getMoviesPromoBoxTitle();
    }

    public void updateMoviePromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        promotionItemFacade.updateMoviePromoBox(title, imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
    }

    public List<Genre> getGenresWithMinimumMovies(int minMoviesCount) {
        return genreFacade.getGenresWithMinimumMovies(minMoviesCount);
    }

    public List<String> getSelectedGenresForMovies() {
        return carouselFacade.getSelectedGenresForMovies();
    }

    public void saveSelectedGenresForMovies(List<String> selectedGenres) {
        carouselFacade.saveSelectedGenresForMovies(selectedGenres);
    }

    public String getNormalizedMovieTitle(String title){
        return movieFacade.getNormalizedMovieTitle(title);
    }









}
