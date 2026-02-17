package org.example.progetto.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component // <--- QUESTA ANNOTAZIONE È FONDAMENTALE PER RISOLVERE L'ERRORE
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        // Usa il claim "email" o "preferred_username" come nome principale se disponibile
        String principalClaimName = jwt.getClaimAsString("email");
        if (principalClaimName == null) {
            principalClaimName = jwt.getClaimAsString("preferred_username");
        }
        if (principalClaimName == null) {
            principalClaimName = jwt.getSubject();
        }
        
        return new JwtAuthenticationToken(jwt, authorities, principalClaimName);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Estrae i ruoli dalla sezione 'realm_access' del token Keycloak
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        // Converte i ruoli in GrantedAuthority con prefisso "ROLE_"
        // Es: "admin" diventa "ROLE_ADMIN"
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}