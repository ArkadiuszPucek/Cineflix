package pl.puccini.cineflix.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class WatchedItemDto {
    private Long episodeId;
    private String movieImdbId;
    private String title;
    private LocalDateTime viewedOn;
    private String type;
    private String imageUrl;
    private Integer seasonNumber;
    private Integer episodeNumber;
}
