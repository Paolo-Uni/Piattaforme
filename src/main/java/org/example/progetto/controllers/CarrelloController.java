package org.example.progetto.controllers;

import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.OggettoCarrelloDTO;
import org.example.progetto.support.ResponseMessage;
import org.example.progetto.exceptions.*;
import org.example.progetto.services.CarrelloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carrello")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CarrelloController {

    private final CarrelloService carrelloService;

    // Helper per ottenere l'email dal token
    private String getEmailFromToken(Authentication authentication) {
        if (authentication == null) {
            throw new InvalidCartOperationException("Utente non autenticato");
        }
        // Assumiamo che il Principal (subject) del token sia l'email
        return authentication.getName();
    }

    @PostMapping("/aggiungi")
    public ResponseEntity<ResponseMessage> aggiungiAlCarrello(@RequestParam Long idProdotto, 
                                                              @RequestParam int quantita, 
                                                              Authentication authentication) {
        try {
            String email = getEmailFromToken(authentication);
            carrelloService.aggiungiAlCarrello(email, idProdotto, quantita);
            return ResponseEntity.ok(new ResponseMessage("Prodotto aggiunto al carrello!"));
        } catch (ProductNotFoundException | ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        } catch (InvalidQuantityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @GetMapping("/vedi")
    public ResponseEntity<List<OggettoCarrelloDTO>> vediCarrello(Authentication authentication) {
        String email = getEmailFromToken(authentication);
        return ResponseEntity.ok(carrelloService.getCartItemsByEmail(email));
    }

    @PostMapping("/rimuovi")
    public ResponseEntity<ResponseMessage> decrementaQuantita(@RequestParam Long idProdotto, 
                                                              Authentication authentication) {
        try {
            String email = getEmailFromToken(authentication);
            carrelloService.decrementaQuantitaOggettoCarrello(email, idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Quantità decrementata."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    // --- NUOVO ENDPOINT PER LA CANCELLAZIONE DIRETTA ---
    @DeleteMapping("/elimina-prodotto")
    public ResponseEntity<ResponseMessage> eliminaProdotto(@RequestParam Long idProdotto, 
                                                           Authentication authentication) {
        try {
            String email = getEmailFromToken(authentication);
            carrelloService.rimuoviProdottoDalCarrello(email, idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Prodotto rimosso dal carrello."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/aumenta")
    public ResponseEntity<ResponseMessage> incrementaQuantita(@RequestParam Long idProdotto, 
                                                              Authentication authentication) {
        try {
            String email = getEmailFromToken(authentication);
            carrelloService.incrementaQuantitaOggettoCarrello(email, idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Quantità incrementata."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/svuota")
    public ResponseEntity<ResponseMessage> svuotaCarrello(Authentication authentication) {
        String email = getEmailFromToken(authentication);
        carrelloService.svuotaCarrello(email);
        return ResponseEntity.ok(new ResponseMessage("Carrello svuotato."));
    }

    @PostMapping("/ordina")
    public ResponseEntity<ResponseMessage> ordina(@RequestBody Map<String, String> body, 
                                                  Authentication authentication) {
        try {
            String email = getEmailFromToken(authentication);
            String indirizzo = body.get("indirizzoSpedizione");
            if (indirizzo == null || indirizzo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Indirizzo di spedizione mancante."));
            }
            carrelloService.ordina(email, indirizzo);
            return ResponseEntity.ok(new ResponseMessage("Ordine effettuato con successo!"));
        } catch (PaymentException e) {
             return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Errore durante l'ordine: " + e.getMessage()));
        }
    }
}