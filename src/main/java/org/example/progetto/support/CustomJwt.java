package org.example.progetto.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

@Setter
@Getter
public class CustomJwt extends JwtAuthenticationToken {

    private String firstname;
    private String lastname;
    private String email; // Aggiunto per facilitare l'accesso nei Controller

    public CustomJwt(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
        this.email = jwt.getClaimAsString("email");
    }
}