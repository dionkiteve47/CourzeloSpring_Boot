package tn.esprit.user.security;

import tn.esprit.user.security.jwt.AuthEntryPointJwt;
import tn.esprit.user.security.jwt.AuthTokenFilter;
import tn.esprit.user.services.Implementations.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    UserService userService;
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/api/Modules/**").permitAll()
                        .requestMatchers("/api/v1/institution/**").permitAll()
                        .requestMatchers("/api/v1/class/**").permitAll()
                        .requestMatchers("/api/data/**").permitAll()
                        .requestMatchers("/api/TimeTable/**").permitAll()
                        .requestMatchers("/api/v1/class/getClassUsers/Teacher").permitAll()
                        .requestMatchers("/api/pdf/classes/**").permitAll()
                        .requestMatchers("/api/pdf/departments/**").permitAll()
                        .requestMatchers("/api/elementModules/**").permitAll()
                        .requestMatchers("/api/fieldOfStudies/**").permitAll()
                        .requestMatchers("/api/departments/**").permitAll()
                        .requestMatchers("/api/Modules/**").permitAll()
                        .requestMatchers("/api/v1/user/**").permitAll()
                        .requestMatchers("/api/v1/schedule/**").permitAll()
                        .requestMatchers("/api/v1/program/**").permitAll()
                        .requestMatchers("/api/nonDisponibilities/**").permitAll()
                        .requestMatchers("/quiz/getAll").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/quiz/create").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/quiz/quiz/create").permitAll() // Allow access to this endpoint without authentication

                        .requestMatchers("/quiz/submit/{_id}").permitAll() // Allow access to this endpoint without authentication

                        .requestMatchers("/quiz/update/{_id}").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/quiz/{_id}/submit").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/quiz/getById/{_id}").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/quiz/quizzez/{_id}").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/quiz//{quizId}/statistics").permitAll() // Allow access to this endpoint without authentication

                        .requestMatchers("/questions").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/questions/allQuestions").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/questions/category/{category}").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/questions/add").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/questions/update/{_id}").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/questions/delete").permitAll() // Allow access to this endpoint without authentication
                        .requestMatchers("/questions/all").permitAll() // Allow access to this endpoint without authentication

                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }



}
