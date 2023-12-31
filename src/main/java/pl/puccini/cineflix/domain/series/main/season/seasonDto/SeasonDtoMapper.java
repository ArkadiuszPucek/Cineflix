package pl.puccini.cineflix.domain.series.main.season.seasonDto;

import pl.puccini.cineflix.domain.series.main.season.model.Season;

public class SeasonDtoMapper {
    public static SeasonDto map(Season season){
        return new SeasonDto(
                season.getId(),
                season.getSeasonNumber()
        );
    }
}
