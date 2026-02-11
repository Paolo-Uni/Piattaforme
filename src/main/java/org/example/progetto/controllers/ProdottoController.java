package org.example.progetto.controllers;

import org.example.progetto.entities.Prodotto;
import org.example.progetto.exceptions.ProductAlreadyExistsException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.services.ProdottoService;
import org.example.progetto.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prodotto")
public class ProdottoController {

    @Autowired
    private ProdottoService prodottoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> aggiungiProdotto(@RequestBody Prodotto prod) {
        try {
            prodottoService.aggiungiProdotto(prod);
            return ResponseEntity.ok(new ResponseMessage("Prodotto aggiunto con successo"));
        } catch (ProductAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete") // Corretto typo "delate"
    public ResponseEntity<?> cancellaProdotto(@RequestParam Long idProdotto) {
        try {
            prodottoService.cancellaProdotto(idProdotto);
            return ResponseEntity.ok(new ResponseMessage("Prodotto cancellato con successo"));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Prodotto non trovato"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/add-stock")
    public ResponseEntity<?> aumentaQuantitaProdotto(@RequestParam Long idProdotto, @RequestParam Integer quantita) {
        try {
            prodottoService.aumentaQuantitaProdotto(idProdotto, quantita);
            return ResponseEntity.ok(new ResponseMessage("Quantità aggiornata con successo"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    /**
     * NUOVO: Ricerca dinamica multicriterio
     */
    @GetMapping("/search")
    public ResponseEntity<?> ricercaDinamica(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String colore,
            @RequestParam(required = false) String taglia) {
        
        List<Prodotto> result = prodottoService.ricercaDinamica(nome, marca, categoria, colore, taglia);
        if (result.isEmpty()) {
            return ResponseEntity.ok(new ResponseMessage("Nessun risultato trovato"));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping()
    public List<Prodotto> getAll() {
        return prodottoService.getProdotti();
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getAllPaged(
            @RequestParam(defaultValue = "0") int pageNumber, 
            @RequestParam(defaultValue = "10") int pageSize, 
            @RequestParam(defaultValue = "id") String sortBy) {
        List<Prodotto> ris = prodottoService.getProdotti(pageNumber, pageSize, sortBy);
        return ris.isEmpty() ? ResponseEntity.ok(new ResponseMessage("Fine risultati")) : ResponseEntity.ok(ris);
    }
}