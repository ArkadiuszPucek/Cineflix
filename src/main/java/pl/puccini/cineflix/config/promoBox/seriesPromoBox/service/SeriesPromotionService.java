package pl.puccini.cineflix.config.promoBox.seriesPromoBox.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.config.promoBox.seriesPromoBox.model.SeriesPromoBox;
import pl.puccini.cineflix.config.promoBox.seriesPromoBox.repository.SeriesPromoBoxRepository;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDtoMapper;
import pl.puccini.cineflix.domain.series.main.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.userLists.UserListFacade;
import pl.puccini.cineflix.domain.user.userRatings.UserRatingFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeriesPromotionService {
    private final SeriesPromoBoxRepository seriesPromoBoxRepository;
    private final SeriesRepository seriesRepository;
    private final SeriesFacade seriesFacade;
    private final UserListFacade userListFacade;
    private final UserRatingFacade userRatingFacade;

    public SeriesPromotionService(SeriesPromoBoxRepository seriesPromoBoxRepository, SeriesRepository seriesRepository, SeriesFacade seriesFacade, UserListFacade userListFacade, UserRatingFacade userRatingFacade) {
        this.seriesPromoBoxRepository = seriesPromoBoxRepository;
        this.seriesRepository = seriesRepository;
        this.seriesFacade = seriesFacade;
        this.userListFacade = userListFacade;
        this.userRatingFacade = userRatingFacade;
    }


    public List<SeriesDto> findAllPromotedSeries() {
        return seriesRepository.findAllByPromotedIsTrue().stream()
                .map(SeriesDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<SeriesDto> getSeriesPromoBox(Long userId) {
        SeriesPromoBox promoBox = getCurrentSeriesPromoBox();
        if (promoBox == null) {
            return Collections.emptyList();
        }

        String[] imdbIds = promoBox.getImdbIds().split(",");
        return Arrays.stream(imdbIds)
                .map(seriesFacade::getSeriesByImdbId)
                .map(SeriesDtoMapper::map)
                .peek(seriesDto -> {
                    seriesDto.setOnUserList(userListFacade.isOnList(userId, seriesDto.getImdbId()));
                    seriesDto.setUserRating(userRatingFacade.getCurrentUserRatingForSeries(seriesDto.getImdbId(), userId).orElse(null));
                })
                .collect(Collectors.toList());
    }

    public String getSeriesPromoBoxTitle() {
        SeriesPromoBox promoBox = getCurrentSeriesPromoBox();
        return promoBox != null ? promoBox.getSeriesPromoBoxTitle() : "Trending Series";
    }

    private SeriesPromoBox getCurrentSeriesPromoBox() {
        return seriesPromoBoxRepository.findTopByOrderByIdDesc();
    }

    public void updateSeriesPromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        List<String> allImdbIds = Arrays.asList(imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        List<String> validImdbIds = new ArrayList<>();

        for (String imdbId : allImdbIds) {
            if (seriesFacade.doesSeriesExists(imdbId)) {
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
