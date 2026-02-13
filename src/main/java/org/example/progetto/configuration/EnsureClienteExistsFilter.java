package org.example.progetto.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.progetto.entities.Cliente;
import org.example.progetto.repositories.ClienteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// RIMOSSO @Component per evitare registrazione automatica globale
public class EnsureClienteExistsFilter extends OncePerRequestFilter {

    private final ClienteRepository clienteRepository;

    public EnsureClienteExistsFilter(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Metodo di utilità condiviso per estrarre l'identificativo univoco (email o username)
     * in modo coerente tra Filtro e Controller.
     */
    public static String getEmailOrUsernameFromJwt(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");
        
        // Priorità all'email, fallback su username, infine subject
        if (email != null && !email.isBlank()) {
            return email;
        }
        if (username != null && !username.isBlank()) {
            return username;
        }
        return jwt.getSubject();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            
            // Usiamo la logica centralizzata
            String userId = getEmailOrUsernameFromJwt(jwt);

            // Se l'utente non esiste, lo creiamo
            if (userId != null && !clienteRepository.existsByEmail(userId)) {
                String firstName = jwt.getClaimAsString("given_name");
                String lastName = jwt.getClaimAsString("family_name");

                Cliente newCliente = new Cliente();
                newCliente.setEmail(userId); // Salviamo l'ID identificato (email o username)
                newCliente.setNome(firstName != null ? firstName : "Utente");
                newCliente.setCognome(lastName != null ? lastName : "Nuovo");
                newCliente.setIndirizzo("Da completare");
                newCliente.setTelefono("");
                
                try {
                    clienteRepository.save(newCliente);
                    System.out.println("SYNC BACKEND: Nuovo cliente creato/sincronizzato: " + userId);
                } catch (Exception e) {
                    System.err.println("SYNC BACKEND: Errore durante la creazione automatica: " + e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}