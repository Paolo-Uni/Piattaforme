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

@Component
public class EnsureClienteExistsFilter extends OncePerRequestFilter {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt jwt){
            String email = jwt.getClaimAsString("email");
            // Controllo se l'email esiste, altrimenti creo l'utente
            if (!clienteRepository.existsByEmail(email)) {
                Cliente nuovo = new Cliente();
                nuovo.setNome(jwt.getClaimAsString("given_name"));
                nuovo.setCognome(jwt.getClaimAsString("family_name"));
                nuovo.setEmail(email);
                // Imposta un valore di default per i campi obbligatori se necessario
                nuovo.setTelefono("0000000000"); 
                clienteRepository.save(nuovo);
            }
        }

        filterChain.doFilter(request, response);
    }
}