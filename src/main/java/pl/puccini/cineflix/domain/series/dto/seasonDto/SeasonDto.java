package pl.puccini.cineflix.domain.series.dto.seasonDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SeasonDto {
    private Long id;
    private int seasonNumber;

    public SeasonDto() {

    }
}
