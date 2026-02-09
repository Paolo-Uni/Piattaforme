package org.example.progetto.controllers;

import org.example.progetto.dto.OrdineDTO;
import org.example.progetto.entities.Cliente;
import org.example.progetto.entities.Ordine;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.repositories.ClienteRepository;
import org.example.progetto.repositories.OrdineRepository;
import org.example.progetto.services.OrdineService;
import org.example.progetto.support.CustomJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/ordine")
public class OrdineController {

    @Autowired
    private OrdineService ordineService;

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private OrdineRepository ordineRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{idOrdine}/annulla")
    public ResponseEntity<String> annullaOrdine(@PathVariable Long idOrdine, @RequestParam String motivo) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        Cliente cliente = clienteRepository.findByEmail(email);
        Ordine ordine = ordineRepository.findById(idOrdine).orElse(null);
        if (cliente == null || ordine == null) {
            return new ResponseEntity<>("Utente o Ordine non trovati.", HttpStatus.NOT_FOUND);
        }
        try {
            ordineService.annullaOrdine(cliente, ordine, motivo);
            return new ResponseEntity<>("Ordine annullato con successo.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'annullamento dell'ordine: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/miei-ordini")
    public ResponseEntity<List<OrdineDTO>> getMieiOrdini() {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        try {
            List<OrdineDTO> ordini = ordineService.getOrdiniByEmail(email);
            return ResponseEntity.ok(ordini);
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
