package org.example.progetto.configuration;

import org.example.progetto.support.CustomJwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomJwtConverter customJwtConverter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CustomJwtConverter customJwtConverter, CorsConfigurationSource corsConfigurationSource) {
        this.customJwtConverter = customJwtConverter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // PERMETTI L'ACCESSO PUBBLICO AI PRODOTTI (SOLO LETTURA)
                        .requestMatchers(HttpMethod.GET, "/prodotto/**").permitAll()
                        // PERMETTI L'ACCESSO PUBBLICO ALLA REGISTRAZIONE UTENTI (se necessario)
                        // .requestMatchers(HttpMethod.POST, "/cliente/registrazione").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}