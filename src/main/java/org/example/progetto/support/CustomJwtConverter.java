package org.example.progetto.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken; // Importante!
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

// KEY FIX: Deve implementare Converter<Jwt, AbstractAuthenticationToken>
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        
        // Creiamo il nostro oggetto CustomJwt
        var customJwt = new CustomJwt(jwt, authorities);
        
        // Impostiamo i dati extra
        customJwt.setFirstname(jwt.getClaimAsString("given_name"));
        customJwt.setLastname(jwt.getClaimAsString("family_name"));
        
        return customJwt; // CustomJwt estende AbstractAuthenticationToken, quindi è valido
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        var authorities = new ArrayList<GrantedAuthority>();
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") != null) {
            var roles = realmAccess.get("roles");
            if (roles instanceof List<?> list) {
                list.forEach(role ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }
        }
        return authorities;
    }
}