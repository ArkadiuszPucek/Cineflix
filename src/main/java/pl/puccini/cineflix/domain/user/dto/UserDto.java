package pl.puccini.cineflix.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.*\\d).{8,}$", message = "Provided password is incorrect")
    private String password;
}
