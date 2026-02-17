package org.example.progetto.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.ClienteDTO;
import org.example.progetto.dto.ClienteUpdateRequest;
import org.example.progetto.dto.Request;
import org.example.progetto.services.ClienteService;
import org.example.progetto.support.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clienti")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/registra")
    public ResponseEntity<?> registraCliente(@RequestBody @Valid Request request) {
        try {
            ClienteDTO nuovoCliente = clienteService.registraCliente(request);
            return ResponseEntity.ok(nuovoCliente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfilo(Authentication authentication) {
        try {
            String email = authentication.getName();
            // Recupera l'entità e la converte in DTO per includere l'indirizzo
            return ResponseEntity.ok(clienteService.toDTO(clienteService.getClienteByEmail(email)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Utente non trovato."));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> aggiornaProfilo(@RequestBody @Valid ClienteUpdateRequest request, Authentication authentication) {
        try {
            String email = authentication.getName();
            ClienteDTO aggiornato = clienteService.updateCliente(email, request);
            return ResponseEntity.ok(aggiornato);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }
}