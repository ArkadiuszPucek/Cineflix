package pl.puccini.cineflix.config.carousel.series.dto;

import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;

import java.util.List;
@Getter
@Setter
public class SeriesCarouselConfigDto {

    private String genre;
    private List<SeriesDto> series;
    private boolean isActive;

}
