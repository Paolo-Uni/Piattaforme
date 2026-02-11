package org.example.progetto.controllers;

import org.example.progetto.entities.Prodotto;
import org.example.progetto.exceptions.ProductAlreadyExistsException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.services.ProdottoService;
import org.example.progetto.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @DeleteMapping("/delete")
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

    // NUOVO METODO AGGIUNTO: Riduzione manuale stock (Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/remove-stock")
    public ResponseEntity<?> diminuisciQuantitaProdotto(@RequestParam Long idProdotto, @RequestParam Integer quantita) {
        try {
            prodottoService.diminuisciQuantitaProdotto(idProdotto, quantita);
            return ResponseEntity.ok(new ResponseMessage("Stock ridotto con successo"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    // === METODO MANCANTE AGGIUNTO ===
    @GetMapping("/{id}")
    public ResponseEntity<?> getProdotto(@PathVariable Long id) {
        try {
            // Assicurati che nel ProdottoService esista un metodo getProdotto(Long id)
            // che restituisca un Prodotto o lanci ProductNotFoundException
            Prodotto p = prodottoService.getProdottoById(id);
            return ResponseEntity.ok(p);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Prodotto non trovato"));
        }
    }
    // ================================

    @GetMapping("/search")
    public ResponseEntity<Page<Prodotto>> ricercaDinamica(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String colore,
            @RequestParam(required = false) String taglia,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> result = prodottoService.ricercaDinamica(nome, marca, categoria, colore, taglia, paging);
        
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