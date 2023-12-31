package pl.puccini.cineflix.config.carousel.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.config.carousel.repository.CarouselConfigRepository;
import pl.puccini.cineflix.config.carousel.series.model.SeriesCarouselConfig;
import pl.puccini.cineflix.config.carousel.series.repository.SeriesCarouselConfigRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class SeriesCarouselService implements CarouselConfigRepository<SeriesCarouselConfig> {
    private final SeriesCarouselConfigRepository seriesCarouselConfigRepository;

    public SeriesCarouselService(SeriesCarouselConfigRepository seriesCarouselConfigRepository) {
        this.seriesCarouselConfigRepository = seriesCarouselConfigRepository;
    }

    @Override
    public void saveSelectedGenres(List<String> selectedGenres) {
        SeriesCarouselConfig config = getConfigById(1L);
        String joinedGenres = selectedGenres.isEmpty() ? "" : String.join(",", selectedGenres);
        config.setActiveGenres(joinedGenres);
        seriesCarouselConfigRepository.save(config);
    }
    @Override
    public List<String> getSelectedGenres() {
        SeriesCarouselConfig config = getTopByOrderByIdDesc();
        return config != null && config.getActiveGenres() != null ?
                Arrays.asList(config.getActiveGenres().split(",")) :
                new ArrayList<>();
    }
    @Override
    public SeriesCarouselConfig getConfigById(Long id) {
        return seriesCarouselConfigRepository.findById(id).orElse(new SeriesCarouselConfig());
    }
    @Override
    public SeriesCarouselConfig getTopByOrderByIdDesc() {
        return seriesCarouselConfigRepository.findTopByOrderByIdDesc();
    }
}
