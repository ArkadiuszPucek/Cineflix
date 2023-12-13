package pl.puccini.cineflix.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.movie.dto.MovieDtoMapper;
import pl.puccini.cineflix.domain.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDtoMapper;
import pl.puccini.cineflix.domain.series.model.SeriesPromoBox;
import pl.puccini.cineflix.domain.series.repository.SeriesPromoBoxRepository;
import pl.puccini.cineflix.domain.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.service.UserRatingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeriesPromotionService {
    private final SeriesPromoBoxRepository seriesPromoBoxRepository;
    private final SeriesRepository seriesRepository;
    private final UserRatingService userRatingService;
    private final SeriesFacade seriesFacade;

    public SeriesPromotionService(SeriesPromoBoxRepository seriesPromoBoxRepository, SeriesRepository seriesRepository, UserRatingService userRatingService, SeriesFacade seriesFacade) {
        this.seriesPromoBoxRepository = seriesPromoBoxRepository;
        this.seriesRepository = seriesRepository;
        this.userRatingService = userRatingService;
        this.seriesFacade = seriesFacade;
    }

    public List<SeriesDto> findAllPromotedSeries() {
        return seriesRepository.findAllByPromotedIsTrue().stream()
                .map(SeriesDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<SeriesDto> getSeriesPromoBox(Long userId) {
        SeriesPromoBox promoBox = seriesPromoBoxRepository.findTopByOrderByIdDesc();
        if (promoBox == null) {
            return Collections.emptyList();
        }

        String[] imdbIds = promoBox.getImdbIds().split(",");
        return Arrays.stream(imdbIds)
                .map(seriesRepository::findSeriesByImdbId)
                .map(SeriesDtoMapper::map)
                .peek(seriesDto -> {
                    seriesDto.setOnUserList(seriesFacade.isSeriesOnUserList(userId, seriesDto.getImdbId()));
                    seriesDto.setUserRating(userRatingService.getCurrentUserRatingForSeries(seriesDto.getImdbId(), userId).orElse(null));
                })
                .collect(Collectors.toList());
    }

    public String getSeriesPromoBoxTitle() {
        SeriesPromoBox promoBox = seriesPromoBoxRepository.findTopByOrderByIdDesc();
        return promoBox != null ? promoBox.getSeriesPromoBoxTitle() : "Trending Series";
    }

    public void updateSeriesPromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        List<String> allImdbIds = Arrays.asList(imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        List<String> validImdbIds = new ArrayList<>();

        for (String imdbId : allImdbIds) {
            if (seriesRepository.existsByImdbId(imdbId)) {
                validImdbIds.add(imdbId);
            } else {
                throw new SeriesNotFoundException("Series not found");
            }
        }

        String joinedImdbIds = String.join(",", validImdbIds);

        SeriesPromoBox seriesPromoBox = new SeriesPromoBox();
        seriesPromoBox.setSeriesPromoBoxTitle(title);
        seriesPromoBox.setImdbIds(joinedImdbIds);
        seriesPromoBoxRepository.save(seriesPromoBox);
    }
}
