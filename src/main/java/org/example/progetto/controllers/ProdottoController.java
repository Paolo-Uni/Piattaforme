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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Map<String, String> response = new HashMap<>();
            response.put("message", "Prodotto aggiunto con successo");
            return ResponseEntity.ok(response);
        } catch (ProductAlreadyExistsException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Il prodotto esiste gia");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delate")
    public ResponseEntity<?> cancellaProdotto(@RequestParam Long idProdotto) {
        try {
            prodottoService.cancellaProdotto(idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Prodotto cancellato con successo");
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Prodotto non trovato");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/add")
    public ResponseEntity<?> aumentaQuantitaProdotto(@RequestParam Long idProdotto, @RequestParam Integer quantita) {
        try {
            prodottoService.aumentaQuantitaProdotto(idProdotto, quantita);
            Map<String, String> response = new HashMap<>();
            response.put("message", "La quantità richiesta del prodotto è stata aggiunta con successo");
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Prodotto non trovato");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/decrease")
    public ResponseEntity<?> diminuisciQuantitaProdotto(@RequestParam Long idProdotto, @RequestParam Integer quantita) {
        try {
            prodottoService.diminuisciQuantitaProdotto(idProdotto, quantita);
            Map<String, String> response = new HashMap<>();
            response.put("message", "La quantità richiesta del prodotto è stata decrementata con successo");
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Prodotto non trovato");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @GetMapping()
    public List<Prodotto> getAll() {
        return prodottoService.getProdotti();
    }

    @GetMapping("/paged")
    public ResponseEntity getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Prodotto> ris = prodottoService.getProdotti(pageNumber, pageSize, sortBy);
        if (ris.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(ris, HttpStatus.OK);
    }

    @GetMapping("/search/name/{nome}")
    public ResponseEntity getByName(@PathVariable(required = false) String nome) {
        List<Prodotto> result = prodottoService.getProdottiByNome(nome);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/paged/name/{nome}")
    public ResponseEntity getByName(@PathVariable(required = false) String nome, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Prodotto> result = prodottoService.getProdottiByNome(nome, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/marca/{marca}")
    public ResponseEntity getByMarca(@PathVariable(required = false) String marca) {
        List<Prodotto> result = prodottoService.getProdottiByMarca(marca);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/paged/marca/{marca}")
    public ResponseEntity getByMarca(@PathVariable(required = false) String marca, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Prodotto> result = prodottoService.getProdottiByMarca(marca, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/categoria/{categoria}")
    public ResponseEntity getByCategoria(@PathVariable(required = false) String categoria) {
        List<Prodotto> result = prodottoService.getProdottiByCategoria(categoria);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/paged/categoria/{categoria}")
    public ResponseEntity getByCategoria(@PathVariable(required = false) String categoria, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Prodotto> result = prodottoService.getProdottiByCategoria(categoria, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/colore/{colore}")
    public ResponseEntity getByColore(@PathVariable(required = false) String colore) {
        List<Prodotto> result = prodottoService.getProdottiByColore(colore);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/paged/colore/{colore}")
    public ResponseEntity getByColore(@PathVariable(required = false) String colore, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Prodotto> result = prodottoService.getProdottiByColore(colore, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/taglia/{taglia}")
    public ResponseEntity getByTaglia(@PathVariable(required = false) String taglia) {
        List<Prodotto> result = prodottoService.getProdottiByTaglia(taglia);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/paged/taglia/{taglia}")
    public ResponseEntity getByTaglia(@PathVariable(required = false) String taglia, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Prodotto> result = prodottoService.getProdottiByTaglia(taglia, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Nessun Risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
