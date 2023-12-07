package pl.puccini.cineflix.domain.genre;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.movie.repository.SeriesCarouselConfigRepository;
import pl.puccini.cineflix.domain.series.model.SeriesCarouselConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final SeriesCarouselConfigRepository seriesCarouselConfigRepository;

    public GenreService(GenreRepository genreRepository, SeriesCarouselConfigRepository seriesCarouselConfigRepository) {
        this.genreRepository = genreRepository;
        this.seriesCarouselConfigRepository = seriesCarouselConfigRepository;
    }
    public List<Genre> getAllGenres(){
        return genreRepository.findAll();
    }

    public Genre getGenreByType(String genreType) {
        return genreRepository.findByGenreType(genreType);
    }

    public List<Genre> getGenresWithMinimumSeries(int minSeriesCount) {
        return genreRepository.findGenresWithMinimumSeries(minSeriesCount);
    }

    public void saveSelectedGenres(List<String> selectedGenres) {
        SeriesCarouselConfig config = seriesCarouselConfigRepository.findById(1L).orElse(new SeriesCarouselConfig());
        String joinedGenres = String.join(",", selectedGenres);
        config.setActiveGenres(joinedGenres);
        seriesCarouselConfigRepository.save(config);
    }

    public List<String> getSelectedGenres() {
        SeriesCarouselConfig config = seriesCarouselConfigRepository.findTopByOrderByIdDesc();
        if (config != null && config.getActiveGenres() != null) {
            return Arrays.asList(config.getActiveGenres().split(","));
        }
        return new ArrayList<>();
    }

}
