package pl.puccini.cineflix.config.carousel;

import java.util.List;

public interface CarouselConfigService <T>{
    void saveSelectedGenres(List<String> selectedGenres);

    List<String> getSelectedGenres();

    T getConfigById(Long id);

    T getTopByOrderByIdDesc();
}
