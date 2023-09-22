package pl.puccini.viaplay.domain.series.seasons;

public class SeasonDtoMapper {
    public static SeasonDto map(Season season){
        return new SeasonDto(
                season.getId(),
                season.getSeasonNumber()
        );
    }
}
