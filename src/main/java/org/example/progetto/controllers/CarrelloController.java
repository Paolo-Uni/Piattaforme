package org.example.progetto.controllers;

import org.example.progetto.dto.OggettoCarrelloDTO;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.InvalidOperationException;
import org.example.progetto.exceptions.InvalidQuantityException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.services.CarrelloService;
import org.example.progetto.support.CustomJwt;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carrello")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/aggiungi")
    public ResponseEntity<Map<String, String>> aggiungiAlCarrello(@RequestParam @NotNull @Positive Long idProdotto, @RequestParam(value = "quantita", defaultValue = "1") @Positive int quantita) {

        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            carrelloService.aggiungiAlCarrello(email, idProdotto, quantita);

            return creaRisposta("Prodotto aggiunto al carrello con successo.", HttpStatus.OK);
        } catch (ClienteNotFoundException e) {
            return creaRisposta("Utente non trovato.", HttpStatus.NOT_FOUND);
        } catch (ProductNotFoundException e) {
            return creaRisposta("Prodotto non trovato.", HttpStatus.NOT_FOUND);
        } catch (InvalidQuantityException e) {
            return creaRisposta(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace(); // Log dell'errore per il debugging
            return creaRisposta("Errore interno del server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> creaRisposta(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/svuota")
    public ResponseEntity<Map<String, String>> svuotaTuttoCarrello() {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            carrelloService.svuotaCarrello(email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Carrello svuotato con successo.");
            return ResponseEntity.ok(response);
        } catch (ClienteNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/rimuovi")
    public ResponseEntity<Map<String, String>> rimuoviDalCarrello(
            @RequestParam @NotNull @Positive Long idProdotto) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            carrelloService.rimuoviDalCarrello(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Prodotto rimosso dal carrello con successo.");
            return ResponseEntity.ok(response);
        } catch (ClienteNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/minus")
    public ResponseEntity<Map<String, String>> decrementaProdottoCarrello(@RequestParam @NotNull @Positive Long idProdotto) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        try {
            carrelloService.decrementaQuantitaOggettoCarrello(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantità ridotta con successo.");
            return ResponseEntity.ok(response);
        } catch (ClienteNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/items")
    public ResponseEntity<List<OggettoCarrelloDTO>> getCartItems() {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            List<OggettoCarrelloDTO> cartItems = carrelloService.getCartItemsByEmail(email);
            return ResponseEntity.ok(cartItems);
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/ordina")
    public ResponseEntity<Map<String, String>> ordina(@RequestParam @NotNull String indirizzoSpedizione) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        try {
            carrelloService.ordina(email, indirizzoSpedizione);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ordine effettuato con successo.");
            return ResponseEntity.ok(response);
        } catch (ClienteNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/plus")
    public ResponseEntity<Map<String, String>> incrementaQuantitaProdottoCarrello(@RequestParam @NotNull @Positive Long idProdotto) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        try {
            carrelloService.incrementaQuantitaOggettoCarrello(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantità aumentata con successo.");
            return ResponseEntity.ok(response);
        } catch (ClienteNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (ProductNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Prodotto non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidQuantityException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
}
