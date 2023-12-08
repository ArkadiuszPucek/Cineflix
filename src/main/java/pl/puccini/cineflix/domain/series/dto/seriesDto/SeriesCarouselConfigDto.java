package pl.puccini.cineflix.domain.series.dto.seriesDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SeriesCarouselConfigDto {

    private String genre;
    private List<SeriesDto> series;
    private boolean isActive;

}
