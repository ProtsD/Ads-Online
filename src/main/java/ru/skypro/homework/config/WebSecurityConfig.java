package ru.skypro.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class WebSecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/login",
            "/register"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf()
                .disable()
                .authorizeHttpRequests(
                        authorization ->
                                authorization
                                        .mvcMatchers(AUTH_WHITELIST)
                                        .permitAll()
                                        .mvcMatchers("/users/**")
                                        .authenticated()
                )
                .cors()
                .and()
                .httpBasic(withDefaults());
        return http.build();
    }

    /*
     * У фронта в utils\api.js у переменной password значение уже зашифрованное Bcrypt'ом в методы передаётся,
     * из-за этого в запросах с Basic авторизацией постоянно выпадает 401 ошибка.
     *     getUserPhoto(imageId, username, password) {
     *              //.......................
     *     }
     *     getComments(adId, username, password) {
     *              //.......................
     *     }
     * через Postman всё работает нормально.
     * Как без данного костыля проблему решить я не знаю.
     * Проверку правильной авторизации убрал для получения картинки профиля. Остальное не трогал.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().mvcMatchers(HttpMethod.GET, "/images/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
