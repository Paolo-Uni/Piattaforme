package org.example.progetto.controllers;

import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.OrdineDTO;
import org.example.progetto.support.ResponseMessage;
import org.example.progetto.entities.Ordine;
import org.example.progetto.services.OrdineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordini")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OrdineController {

    private final OrdineService ordineService;

    @GetMapping("/miei-ordini")
    public ResponseEntity<List<OrdineDTO>> getMieiOrdini(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ordineService.getOrdiniByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrdine(@PathVariable Long id) {
        try {
            Ordine ordine = ordineService.getOrdineById(id);
            // Qui potresti voler controllare se l'ordine appartiene all'utente loggato per sicurezza
            return ResponseEntity.ok(ordine); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Ordine non trovato."));
        }
    }

    @PostMapping("/annulla/{id}")
    public ResponseEntity<ResponseMessage> annullaOrdine(@PathVariable Long id, 
                                                         @RequestBody Map<String, String> body,
                                                         Authentication authentication) {
        try {
            String email = authentication.getName();
            String motivo = body.getOrDefault("motivo", "Nessun motivo specificato");
            
            // Chiamata al metodo del service aggiornato
            ordineService.annullaOrdine(id, email, motivo);
            
            return ResponseEntity.ok(new ResponseMessage("Richiesta di annullamento e rimborso processata."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Errore: " + e.getMessage()));
        }
    }
}