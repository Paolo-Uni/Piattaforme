package org.example.progetto.controllers;

import org.example.progetto.dto.ClienteDTO;
import org.example.progetto.dto.ClienteUpdateRequest;
import org.example.progetto.dto.Request;
import org.example.progetto.entities.Cliente;
import org.example.progetto.services.ClienteService;
import org.example.progetto.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/clienti")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/registrazione")
    public ResponseEntity<?> registra(@RequestBody @Valid Request request) {
        try {
            return ResponseEntity.ok(clienteService.registraCliente(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<ClienteDTO> getCurrentUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwt.getClaimAsString("email");
        Cliente user = clienteService.getClienteByEmail(email);
        return user != null ? ResponseEntity.ok(clienteService.toDTO(user)) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<?> updateCurrentUser(@RequestBody @Valid ClienteUpdateRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwt.getClaimAsString("email");
        
        Cliente user = clienteService.getClienteByEmail(email);
        if (user != null) {
            user.setNome(request.getNome());
            user.setCognome(request.getCognome());
            clienteService.saveCliente(user);
            return ResponseEntity.ok(clienteService.toDTO(user));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Utente non trovato"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminaCliente(@PathVariable Long id) {
        try {
            clienteService.deleteCliente(id);
            return ResponseEntity.ok(new ResponseMessage("Cliente eliminato"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }
}