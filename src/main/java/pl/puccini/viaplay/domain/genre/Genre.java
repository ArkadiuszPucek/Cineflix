package pl.puccini.viaplay.domain.genre;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Genre {
    @Id
    private String genreType;
}
