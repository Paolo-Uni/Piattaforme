package org.example.progetto.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.example.progetto.dto.ClienteDTO;
import org.example.progetto.dto.ClienteUpdateRequest;
import org.example.progetto.dto.Request;
import org.example.progetto.entities.Cliente;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.CredentialsAlredyExistException;
import org.example.progetto.services.ClienteService;
import org.example.progetto.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clienti")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/registrazione")
    public ResponseEntity<?> create(@RequestBody @Valid Request request) {
        try {
            ClienteDTO nuovo = clienteService.registraCliente(request);
            return ResponseEntity.ok(nuovo);
        } catch (CredentialsAlredyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Credenziali gia in uso"));
        }
    }

    @PreAuthorize(value="hasRole('ADMIN')")
    @GetMapping(value="/search/{id}")
    public @ResponseBody ResponseEntity<ClienteDTO> readById(@PathVariable Long id) throws ClienteNotFoundException {
        ClienteDTO cliente = clienteService.getCliente(id);
        if(cliente == null) { return ResponseEntity.notFound().build(); }
        return ResponseEntity.ok(cliente);
    }

    @PreAuthorize(value="hasRole('ADMIN')")
    @GetMapping(value="/search/by_name/{name}")
    public @ResponseBody List<ClienteDTO> readByName(@PathVariable String name) {
        return clienteService.getClientiByNome(name);
    }

    @PreAuthorize(value="hasRole('ADMIN')")
    @GetMapping(value="/search/by_surname/{surname}")
    public @ResponseBody List<ClienteDTO> readBySurname(@PathVariable String surname) {
        return clienteService.getClientiByCognome(surname);
    }

    @PreAuthorize(value="hasRole('ADMIN')")
    @GetMapping(value="/search/by_name_surname/{name}/{surname}")
    public @ResponseBody List<ClienteDTO> readByNameSurname(@PathVariable String name, @PathVariable String surname) {
        return clienteService.getClientiByNomeAndCognome(name,surname);
    }

    @PreAuthorize(value="hasRole('ADMIN')")
    @GetMapping("search/by_email")
    public ResponseEntity<?> getClienteByEmail(@RequestParam String email) {
        Cliente cliente = clienteService.getClienteByEmail(email);
        if (cliente == null) {
            return ResponseEntity.status(404).body("Cliente non trovato.");
        }
        return ResponseEntity.ok(clienteService.toDTO(cliente));
    }

    @PreAuthorize(value="hasRole('ADMIN')")
    @GetMapping("search/by_telefono")
    public ResponseEntity<?> getClienteByPhone(@RequestParam String telefono) {
        Cliente cliente = clienteService.getClienteByTelefono(telefono);
        if (cliente == null) {
            return ResponseEntity.status(404).body("Cliente non trovato.");
        }
        return ResponseEntity.ok(clienteService.toDTO(cliente));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<ClienteDTO> getTuttiClienti() {
        return clienteService.getAllClienti();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<ClienteDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaimAsString("email");

        Cliente currentUser = clienteService.getClienteByEmail(email);
        if (currentUser != null) {
            return ResponseEntity.ok(clienteService.toDTO(currentUser));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<ClienteDTO> updateCurrentUser(@RequestBody @Valid ClienteUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaimAsString("email");

        Cliente currentUser = clienteService.getClienteByEmail(email);
        if (currentUser != null) {
            currentUser.setNome(request.getNome());
            currentUser.setCognome(request.getCognome());
            clienteService.saveCliente(currentUser);
            return ResponseEntity.ok(clienteService.toDTO(currentUser));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminaCliente(@PathVariable Long id) {
        try {
            clienteService.deleteCliente(id);
            return ResponseEntity.ok(new ResponseMessage("Cliente eliminato con successo."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(new ResponseMessage(e.getMessage()));
        }
    }
}
