package org.example.progetto.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Map<String, Object> realmAccess = source.getClaim("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return new JwtAuthenticationToken(source, List.of());
        }
        
        Collection<GrantedAuthority> authorities = ((List<String>) realmAccess.get("roles")).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
                
        return new JwtAuthenticationToken(source, authorities);
    }
}