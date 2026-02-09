package org.example.progetto.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


   /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                );

        return http.build();
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, EnsureClienteExistsFilter ensureClienteExistsFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                //questi endpoint possono essere raggiunti da chiunque (quindi un utente può sfogliare
                //il catalogo senza essere loggato)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/clienti/registrazione",
                                "/prodotto",
                                "/prodotto/paged",
                                "/prodotto/search/name/{nome}",
                                "/prodotto/search/paged/name/{nome}",
                                "/prodotto/search/marca/{marca}",
                                "/prodotto/search/paged/marca/{marca}",
                                "/prodotto/search/categoria/{categoria}",
                                "/prodotto/search/paged/categoria/{categoria}",
                                "/prodotto/search/colore/{colore}",
                                "/prodotto/search/paged/colore/{colore}",
                                "/prodotto/search/taglia/{taglia}",
                                "/prodotto/search/paged/taglia/{taglia}"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverterM()))
                );
        http.addFilterAfter(ensureClienteExistsFilter, org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);
        return http.build();

    }


    /*@Bean
        public Converter<Jwt, ? extends AbstractAuthenticationToken> customJwtConverterM() {
            return new CustomJwtConverter();
        }
    }*/

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverterM() {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = new ArrayList<GrantedAuthority>();

            var realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                var roles = realmAccess.get("roles");
                if (roles instanceof List<?> list) {
                    list.forEach(role ->
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                }
            }

            return authorities;
        });
        return jwtAuthenticationConverter;
    }
}

