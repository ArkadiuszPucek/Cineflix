package pl.puccini.viaplay.domain.series.dto.seasonDto;

import pl.puccini.viaplay.domain.series.model.Season;

public class SeasonDtoMapper {
    public static SeasonDto map(Season season){
        return new SeasonDto(
                season.getId(),
                season.getSeasonNumber()
        );
    }
}
