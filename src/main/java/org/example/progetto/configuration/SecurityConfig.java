package org.example.progetto.configuration;

import org.example.progetto.support.CustomJwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> customJwtConverter() {
        return new CustomJwtConverter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, EnsureClienteExistsFilter ensureClienteExistsFilter) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Utilizza la configurazione definita in CorsConfig o WebConfig
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/clienti/registrazione",
                        "/prodotto",
                        "/prodotto/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter()))
            );

        // Il filtro deve essere eseguito DOPO l'autenticazione JWT per avere l'email dell'utente
        http.addFilterAfter(ensureClienteExistsFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}