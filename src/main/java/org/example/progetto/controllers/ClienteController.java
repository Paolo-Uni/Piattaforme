package org.example.progetto.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.ClienteDTO;
import org.example.progetto.dto.ClienteUpdateRequest;
import org.example.progetto.dto.Request;
import org.example.progetto.support.ResponseMessage;
import org.example.progetto.entities.Cliente;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.CredentialsAlreadyExistException;
import org.example.progetto.services.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/registra")
    public ResponseEntity<?> registraCliente(@Valid @RequestBody Request request) {
        try {
            ClienteDTO nuovoCliente = clienteService.registraCliente(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovoCliente);
        } catch (CredentialsAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(e.getMessage()));
        }
    }

    // Endpoint per ottenere i dati del PROFILO loggato
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            Cliente cliente = clienteService.getClienteByEmail(email);
            return ResponseEntity.ok(clienteService.toDTO(cliente));
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Profilo non trovato."));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')") // Solo l'admin dovrebbe vedere tutti i clienti
    public ResponseEntity<List<ClienteDTO>> getAllClienti() {
        return ResponseEntity.ok(clienteService.getAllClienti());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getClienteById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clienteService.getCliente(id));
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMessage> deleteCliente(@PathVariable Long id) {
        try {
            clienteService.deleteCliente(id);
            return ResponseEntity.ok(new ResponseMessage("Cliente eliminato con successo."));
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/me/update")
    public ResponseEntity<?> updateProfile(@RequestBody ClienteUpdateRequest request, Authentication authentication) {
        try {
            String email = authentication.getName();
            ClienteDTO aggiornato = clienteService.updateCliente(email, request);
            return ResponseEntity.ok(aggiornato);
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Profilo non trovato."));
        } catch (CredentialsAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(e.getMessage()));
        }
    }
}