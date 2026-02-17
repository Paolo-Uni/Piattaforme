package org.example.progetto.configuration;

import lombok.RequiredArgsConstructor;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtConverter customJwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disabilita CSRF (non necessario per API stateless)
            .cors(cors -> cors.configure(http)) // Usa la configurazione CORS definita nel Bean CorsConfig
            .authorizeHttpRequests(auth -> auth
                // Permetti a TUTTI di fare richieste OPTIONS (necessario per il preflight CORS del browser)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Endpoint pubblici (senza login)
                .requestMatchers("/prodotti/all", "/prodotti/paged", "/prodotti/cerca", "/prodotti/{id}").permitAll()
                .requestMatchers("/prodotti/marche", "/prodotti/categorie", "/prodotti/colori", "/prodotti/taglie").permitAll()
                .requestMatchers("/cliente/registra").permitAll()
                
                // Endpoint Admin (gestiti anche tramite @PreAuthorize nei controller, ma utile come sicurezza aggiuntiva)
                .requestMatchers("/prodotti/admin/**").hasRole("ADMIN")
                .requestMatchers("/cliente/all", "/cliente/{id}").hasRole("ADMIN")

                // Tutto il resto richiede autenticazione
                .anyRequest().authenticated()
            )
            // Configura il server per validare i token JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter))
            )
            // Stateless: non crea sessioni HTTP (JSESSIONID)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}