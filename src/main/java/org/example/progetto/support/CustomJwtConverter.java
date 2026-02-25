package org.example.progetto.support;

import lombok.RequiredArgsConstructor;
import org.example.progetto.entities.Cliente;
import org.example.progetto.repositories.ClienteRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final ClienteRepository clienteRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        // 1. Cerca l'email diretta
        String email = jwt.getClaimAsString("email");
        
        // 2. Fallback sul preferred_username (Keycloak lo include quasi sempre)
        if (email == null) {
            email = jwt.getClaimAsString("preferred_username");
        }
        
        // 3. Fallback estremo sul Subject (ID Keycloak)
        String principalClaimName = (email != null) ? email : jwt.getSubject();

        // SINCRONIZZAZIONE AUTOMATICA: Se l'utente non esiste nel DB, lo crea
        if (principalClaimName != null && !clienteRepository.existsByEmail(principalClaimName)) {
            Cliente nuovoCliente = new Cliente();
            nuovoCliente.setEmail(principalClaimName);

            String nome = jwt.getClaimAsString("given_name");
            nuovoCliente.setNome(nome != null ? nome : "Utente");

            String cognome = jwt.getClaimAsString("family_name");
            nuovoCliente.setCognome(cognome != null ? cognome : "Registrato");

            clienteRepository.save(nuovoCliente);
        }

        return new JwtAuthenticationToken(jwt, authorities, principalClaimName);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}