package pl.puccini.cineflix.config.carousel.repository;

import java.util.List;

public interface CarouselConfigRepository<T>{
    void saveSelectedGenres(List<String> selectedGenres);

    List<String> getSelectedGenres();

    T getConfigById(Long id);

    T getTopByOrderByIdDesc();
}
