package org.example.progetto.configuration;

import org.example.progetto.support.CustomJwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.example.progetto.configuration.EnsureClienteExistsFilter;

// Aggiungi questo Bean o modifica quello esistente
@Bean
public Converter<Jwt, AbstractAuthenticationToken> customJwtConverter() {
    return new CustomJwtConverter();
}

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, EnsureClienteExistsFilter ensureClienteExistsFilter) throws Exception {
            http
                    // ... (resto della config)
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter())) // Usa il tuo converter custom
                    );
            http.addFilterAfter(ensureClienteExistsFilter, org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);
            return http.build();
        }