package org.example.progetto.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component; // Import necessario

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component // <--- Aggiungi questa annotazione per renderlo un Bean gestito da Spring
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        CustomJwt customJwt = new CustomJwt(jwt, authorities);
        
        // Mappatura dei claim standard di Keycloak/OpenID
        customJwt.setFirstname(jwt.getClaimAsString("given_name"));
        customJwt.setLastname(jwt.getClaimAsString("family_name"));
        customJwt.setEmail(jwt.getClaimAsString("email"));
        
        return customJwt;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        var authorities = new ArrayList<GrantedAuthority>();
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        
        if (realmAccess != null && realmAccess.get("roles") != null) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof List<?> list) {
                list.forEach(role ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }
        }
        return authorities;
    }
}