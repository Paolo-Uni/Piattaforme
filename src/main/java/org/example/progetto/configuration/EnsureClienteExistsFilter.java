package org.example.progetto.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.progetto.entities.Cliente;
import org.example.progetto.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class EnsureClienteExistsFilter extends OncePerRequestFilter {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifichiamo che l'utente sia autenticato via JWT
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            
            if (email != null && !clienteRepository.existsByEmail(email)) {
                Cliente nuovo = new Cliente();
                nuovo.setNome(jwt.getClaimAsString("given_name"));
                nuovo.setCognome(jwt.getClaimAsString("family_name"));
                nuovo.setEmail(email);
                
                // FIX: Generiamo un valore univoco temporaneo per il telefono 
                // per evitare violazioni di unicità (UniqueConstraint)
                nuovo.setTelefono("TEMP-" + UUID.randomUUID().toString().substring(0, 8)); 
                
                clienteRepository.save(nuovo);
            }
        }

        filterChain.doFilter(request, response);
    }
}