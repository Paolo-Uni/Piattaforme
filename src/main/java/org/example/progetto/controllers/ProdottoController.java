package org.example.progetto.controllers;

import lombok.RequiredArgsConstructor;
import org.example.progetto.support.ResponseMessage;
import org.example.progetto.entities.Prodotto;
import org.example.progetto.exceptions.ProductAlreadyExistsException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.services.ProdottoService;
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
@RequestMapping("/prodotti")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProdottoController {

    private final ProdottoService prodottoService;

    @GetMapping("/all")
    public ResponseEntity<List<Prodotto>> getAllProdotti() {
        return ResponseEntity.ok(prodottoService.getProdotti());
    }

    @GetMapping("/paged")
    public ResponseEntity<List<Prodotto>> getAllProdottiPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy) {
        return ResponseEntity.ok(prodottoService.getProdotti(page, size, sortBy));
    }

    @GetMapping("/cerca")
    public ResponseEntity<Page<Prodotto>> ricercaDinamica(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String colore,
            @RequestParam(required = false) String taglia,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(prodottoService.ricercaDinamica(nome, marca, categoria, colore, taglia, pageable));
    }
    
    // Endpoint di utilità per i filtri nel frontend
    @GetMapping("/marche")
    public ResponseEntity<List<String>> getMarche() {
        return ResponseEntity.ok(prodottoService.getAllMarche());
    }
    
    @GetMapping("/categorie")
    public ResponseEntity<List<String>> getCategorie() {
        return ResponseEntity.ok(prodottoService.getAllCategorie());
    }

    @GetMapping("/colori")
    public ResponseEntity<List<String>> getColori() {
        return ResponseEntity.ok(prodottoService.getAllColori());
    }

    @GetMapping("/taglie")
    public ResponseEntity<List<String>> getTaglie() {
        return ResponseEntity.ok(prodottoService.getAllTaglie());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProdotto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(prodottoService.getProdottoById(id));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    // --- SEZIONE ADMIN ---

    @PostMapping("/admin/aggiungi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> aggiungiProdotto(@RequestBody Prodotto prodotto) {
        try {
            prodottoService.aggiungiProdotto(prodotto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Prodotto aggiunto con successo"));
        } catch (ProductAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/admin/elimina/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMessage> cancellaProdotto(@PathVariable Long id) {
        try {
            prodottoService.cancellaProdotto(id);
            return ResponseEntity.ok(new ResponseMessage("Prodotto eliminato"));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/admin/stock/aumenta")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMessage> aumentaStock(@RequestParam Long id, @RequestParam int quantita) {
        try {
            prodottoService.aumentaQuantitaProdotto(id, quantita);
            return ResponseEntity.ok(new ResponseMessage("Stock aggiornato"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/admin/stock/diminuisci")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMessage> diminuisciStock(@RequestParam Long id, @RequestParam int quantita) {
        try {
            prodottoService.diminuisciQuantitaProdotto(id, quantita);
            return ResponseEntity.ok(new ResponseMessage("Stock aggiornato"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
    }
}