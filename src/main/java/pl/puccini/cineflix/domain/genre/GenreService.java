package pl.puccini.cineflix.domain.genre;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    public List<Genre> getAllGenres(){
        return genreRepository.findAll();
    }

    public Genre getGenreByType(String genreType) {
        return genreRepository.findByGenreType(genreType);
    }
}
