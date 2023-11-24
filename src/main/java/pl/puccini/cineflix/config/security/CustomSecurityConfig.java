package pl.puccini.cineflix.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class CustomSecurityConfig {
    private static final String USER_ROLE = "USER";
    private static final String EDITOR_ROLE = "EDITOR";
    private static final String ADMIN_ROLE = "ADMIN";


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/images/**", "/styles/**", "/scripts/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole(EDITOR_ROLE, ADMIN_ROLE)
                        .requestMatchers("/master/**").hasRole(ADMIN_ROLE)
                        .requestMatchers("/register/**").permitAll()
                        .requestMatchers("/library/**").hasAnyRole(ADMIN_ROLE, EDITOR_ROLE, USER_ROLE)
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(myAuthenticationSuccessHandler())
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout/**", HttpMethod.GET.name()))
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                if (authentication.isAuthenticated()) {
                    getRedirectStrategy().sendRedirect(request, response, "/");
                } else {
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }
        };
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/img/**",
                "/scripts/**",
                "/styles/**",
                "/error"
        );
    }
}
