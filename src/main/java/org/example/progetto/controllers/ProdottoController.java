package org.example.progetto.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.progetto.entities.Prodotto;
import org.example.progetto.services.ProdottoService;
import org.example.progetto.support.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Assumendo che usi i ruoli
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prodotti")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProdottoController {

    private final ProdottoService prodottoService;

    // Endpoint per la ricerca (utilizzato dalla Home e dalla pagina Prodotti)
    @GetMapping
    public ResponseEntity<Page<Prodotto>> getProdotti(
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getProdotto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(prodottoService.getProdottoById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Prodotto non trovato."));
        }
    }
    
    // Endpoint per i filtri (Marche, Categorie, ecc.)
    @GetMapping("/marche")
    public List<String> getMarche() { return prodottoService.getAllMarche(); }

    @GetMapping("/categorie")
    public List<String> getCategorie() { return prodottoService.getAllCategorie(); }

    @GetMapping("/colori")
    public List<String> getColori() { return prodottoService.getAllColori(); }

    @GetMapping("/taglie")
    public List<String> getTaglie() { return prodottoService.getAllTaglie(); }

    // --- Endpoints Amministrativi ---

    @PostMapping
    @PreAuthorize("hasRole('client_admin')") // Protezione Keycloak se configurata
    public ResponseEntity<ResponseMessage> creaProdotto(@RequestBody @Valid Prodotto prodotto) {
        try {
            prodottoService.aggiungiProdotto(prodotto);
            return ResponseEntity.ok(new ResponseMessage("Prodotto creato con successo."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('client_admin')") // Protezione Keycloak se configurata
    public ResponseEntity<ResponseMessage> cancellaProdotto(@PathVariable Long id) {
        try {
            prodottoService.cancellaProdotto(id);
            return ResponseEntity.ok(new ResponseMessage("Prodotto eliminato."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Errore durante l'eliminazione: " + e.getMessage()));
        }
    }
}