package com.techs.app.config;

import com.techs.app.security.SessionAuthFilter;
import com.techs.app.service.SessionService;
import com.techs.domain.userauth.repository.*;
import com.techs.domain.userauth.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthorizationService authorizationService(UserRepository userRepository,
                                                      RoleRepository roleRepository,
                                                      RightRepository rightRepository) {
        return new AuthorizationService(userRepository, roleRepository, rightRepository);
    }

    @Bean
    public AuthorizationServiceManagement authorizationServiceManagement(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RightRepository rightRepository) {
        return new AuthorizationServiceManagement(userRepository, roleRepository, rightRepository);
    }

    @Bean
    public AuthService authService(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    SessionRepository sessionRepository,
                                    PasswordEncoder passwordEncoder,
                                    AuthorizationService authorizationService) {
        return new AuthService(userRepository, roleRepository, sessionRepository,
                passwordEncoder, authorizationService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                            SessionService sessionService,
                                            AuthorizationService authorizationService) throws Exception {
        SessionAuthFilter sessionAuthFilter = new SessionAuthFilter(sessionService);

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .anyRequest().permitAll()
            )
            .addFilterBefore(sessionAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
