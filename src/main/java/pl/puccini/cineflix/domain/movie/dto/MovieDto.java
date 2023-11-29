package pl.puccini.cineflix.domain.movie.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MovieDto {

    private String imdbId;
    private String title;
    private Integer releaseYear;
    private String imageUrl;
    private String backgroundImageUrl;
    private String mediaUrl;
    private Integer timeline;
    private Integer ageLimit;
    private String description;
    private String staff;
    private String directedBy;
    private String languages;
    private String genre;
    private boolean promoted;
    private Double imdbRating;
    private String imdbUrl;
    private boolean onUserList;

}
