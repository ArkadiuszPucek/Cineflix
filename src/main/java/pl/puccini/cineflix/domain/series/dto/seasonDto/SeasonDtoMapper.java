package pl.puccini.cineflix.domain.series.dto.seasonDto;

import pl.puccini.cineflix.domain.series.model.Season;

public class SeasonDtoMapper {
    public static SeasonDto map(Season season){
        return new SeasonDto(
                season.getId(),
                season.getSeasonNumber()
        );
    }
}
