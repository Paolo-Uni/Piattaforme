package org.example.progetto.controllers;

import org.example.progetto.dto.OggettoCarrelloDTO;
import org.example.progetto.exceptions.*;
import org.example.progetto.services.CarrelloService;
import org.example.progetto.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrello")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/aggiungi")
    public ResponseEntity<?> aggiungiAlCarrello(@RequestParam Long idProdotto, @RequestParam(defaultValue = "1") int quantita) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            carrelloService.aggiungiAlCarrello(email, idProdotto, quantita);
            return ResponseEntity.ok(new ResponseMessage("Prodotto aggiunto e totale carrello aggiornato"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/plus")
    public ResponseEntity<?> incrementaQuantita(@RequestParam Long idProdotto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            carrelloService.incrementaQuantitaOggettoCarrello(email, idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Quantità incrementata"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/minus")
    public ResponseEntity<?> decrementaQuantita(@RequestParam Long idProdotto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            carrelloService.decrementaQuantitaOggettoCarrello(email, idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Quantità decrementata"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/ordina")
    public ResponseEntity<?> effettuaOrdine(@RequestParam String indirizzoSpedizione) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            carrelloService.ordina(email, indirizzoSpedizione);
            return ResponseEntity.ok(new ResponseMessage("Ordine effettuato con successo. Carrello svuotato."));
        } catch (PaymentException e) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/items")
    public ResponseEntity<List<OggettoCarrelloDTO>> getCartItems() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(carrelloService.getCartItemsByEmail(email));
    }
}