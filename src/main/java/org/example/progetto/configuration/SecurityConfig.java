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
// Non usato qui ma comune
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomJwtConverter customJwtConverter;
    private final EnsureClienteExistsFilter ensureClienteExistsFilter;

    public SecurityConfig(CustomJwtConverter customJwtConverter, EnsureClienteExistsFilter ensureClienteExistsFilter) {
        this.customJwtConverter = customJwtConverter;
        this.ensureClienteExistsFilter = ensureClienteExistsFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> auth
                // Endpoint pubblici
                .requestMatchers(HttpMethod.GET, "/api/prodotti/**", "/api/prodotti").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/clienti", "/api/clienti/login").permitAll()
                // Endpoint protetti (richiedono autenticazione)
                .requestMatchers("/api/ordini/**").authenticated()
                .requestMatchers("/api/carrello/**").authenticated()
                .requestMatchers("/api/clienti/me").authenticated() // Fondamentale per il profilo
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter))
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // AGGIUNTA FONDAMENTALE: Il filtro deve girare DOPO l'autenticazione del token
            .addFilterAfter(ensureClienteExistsFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}