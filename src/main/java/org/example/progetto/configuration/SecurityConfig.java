package org.example.progetto.configuration;

import org.example.progetto.repositories.ClienteRepository;
import org.example.progetto.support.CustomJwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomJwtConverter customJwtConverter;
    // Iniettiamo il repository invece del filtro
    private final ClienteRepository clienteRepository;

    public SecurityConfig(CustomJwtConverter customJwtConverter, ClienteRepository clienteRepository) {
        this.customJwtConverter = customJwtConverter;
        this.clienteRepository = clienteRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configure(http)) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/prodotti/**", "/api/prodotti").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/clienti/registrazione", "/api/clienti/login").permitAll()
                .requestMatchers("/api/ordini/**").authenticated()
                .requestMatchers("/api/carrello/**").authenticated()
                .requestMatchers("/api/clienti/me").authenticated()
                .requestMatchers("/clienti/**").permitAll() // Se usi path diversi, controlla qui
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter))
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Creiamo il filtro manualmente qui per assicurarci che giri DOPO l'autenticazione JWT
            .addFilterAfter(new EnsureClienteExistsFilter(clienteRepository), BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}