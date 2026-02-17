package org.example.progetto.controllers;

import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.OggettoCarrelloDTO;
import org.example.progetto.services.CarrelloService;
import org.example.progetto.support.ResponseMessage;
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

    @GetMapping
    public ResponseEntity<List<OggettoCarrelloDTO>> getCarrello(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(carrelloService.getCartItemsByEmail(email));
    }

    @PostMapping("/aggiungi")
    public ResponseEntity<ResponseMessage> aggiungiAlCarrello(@RequestBody Map<String, Object> body, Authentication authentication) {
        try {
            String email = authentication.getName();
            Long idProdotto = Long.valueOf(body.get("idProdotto").toString());
            int quantita = Integer.parseInt(body.get("quantita").toString());
            
            carrelloService.aggiungiAlCarrello(email, idProdotto, quantita);
            return ResponseEntity.ok(new ResponseMessage("Prodotto aggiunto al carrello!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }
    
    // Endpoint per rimuovere completamente un prodotto (cestino)
    @DeleteMapping("/rimuovi/{idProdotto}")
    public ResponseEntity<ResponseMessage> rimuoviProdotto(@PathVariable Long idProdotto, Authentication authentication) {
        try {
            String email = authentication.getName();
            carrelloService.rimuoviProdottoDalCarrello(email, idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Prodotto rimosso dal carrello."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/incrementa/{idProdotto}")
    public ResponseEntity<ResponseMessage> incrementa(@PathVariable Long idProdotto, Authentication authentication) {
        try {
            carrelloService.incrementaQuantitaOggettoCarrello(authentication.getName(), idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Quantità incrementata."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/decrementa/{idProdotto}")
    public ResponseEntity<ResponseMessage> decrementa(@PathVariable Long idProdotto, Authentication authentication) {
        try {
            carrelloService.decrementaQuantitaOggettoCarrello(authentication.getName(), idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Quantità decrementata."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/svuota")
    public ResponseEntity<ResponseMessage> svuotaCarrello(Authentication authentication) {
        carrelloService.svuotaCarrello(authentication.getName());
        return ResponseEntity.ok(new ResponseMessage("Carrello svuotato."));
    }

    @PostMapping("/ordina")
    public ResponseEntity<ResponseMessage> ordina(@RequestBody Map<String, String> body, Authentication authentication) {
        try {
            String email = authentication.getName();
            String indirizzo = body.get("indirizzoSpedizione");
            
            if (indirizzo == null || indirizzo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Indirizzo di spedizione mancante."));
            }

            carrelloService.ordina(email, indirizzo);
            return ResponseEntity.ok(new ResponseMessage("Ordine effettuato con successo!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Errore ordine: " + e.getMessage()));
        }
    }
}