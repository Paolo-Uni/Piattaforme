package org.example.progetto.controllers;

import org.example.progetto.dto.OrdineDTO;
import org.example.progetto.entities.Cliente;
import org.example.progetto.entities.Ordine;
import org.example.progetto.services.ClienteService;
import org.example.progetto.services.OrdineService;
import org.example.progetto.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordine")
public class OrdineController {

    @Autowired
    private OrdineService ordineService;

    @Autowired
    private ClienteService clienteService; // Sostituito ClienteRepository

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{idOrdine}/annulla")
    public ResponseEntity<?> annullaOrdine(@PathVariable Long idOrdine, @RequestParam String motivo) {
        // Estrazione email dal token JWT
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Recupero dei dati tramite Service
        Cliente cliente = clienteService.getClienteByEmail(email);
        
        // Per recuperare l'ordine, usiamo un nuovo metodo nel Service (o quello esistente)
        // per mantenere l'isolamento dai repository
        try {
            // Nota: Ho aggiunto un metodo getOrdineById nel Service per evitare l'uso del repo qui
            Ordine ordine = ordineService.getOrdineById(idOrdine);

            if (cliente == null || ordine == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessage("Cliente o Ordine non trovato"));
            }

            ordineService.annullaOrdine(cliente, ordine, motivo);
            return ResponseEntity.ok(new ResponseMessage("Ordine annullato con successo."));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/miei-ordini")
    public ResponseEntity<List<OrdineDTO>> getMieiOrdini() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ordineService.getOrdiniByEmail(email));
    }
}