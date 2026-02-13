package org.example.progetto.configuration;

import lombok.RequiredArgsConstructor;
import org.example.progetto.support.CustomJwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Abilita @PreAuthorize nei controller
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disabilitiamo CSRF perché usiamo JWT (stateless)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Abilitiamo CORS usando la configurazione globale
                .cors(Customizer.withDefaults())

                // Gestione sessione stateless (nessun cookie di sessione)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configurazione autorizzazioni URL
                .authorizeHttpRequests(auth -> auth
                        // Endpoint pubblici
                        .requestMatchers(HttpMethod.GET, "/prodotti/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/cliente/registra").permitAll()
                        
                        // Endpoint Admin (protezione aggiuntiva oltre al @PreAuthorize)
                        .requestMatchers("/prodotti/admin/**").hasRole("ADMIN")
                        .requestMatchers("/cliente/all").hasRole("ADMIN")
                        
                        // Tutto il resto richiede autenticazione
                        .anyRequest().authenticated()
                )

                // Configurazione Resource Server (JWT)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomJwtConverter()))
                );

        return http.build();
    }
}