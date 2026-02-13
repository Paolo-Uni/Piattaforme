package org.example.progetto.services;

import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.ClienteDTO;
import org.example.progetto.dto.ClienteUpdateRequest;
import org.example.progetto.dto.Request;
import org.example.progetto.entities.Cliente;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.CredentialsAlreadyExistException;
import org.example.progetto.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public ClienteDTO registraCliente(Request req) throws CredentialsAlreadyExistException {
        if (clienteRepository.existsByEmail(req.getEmail())) {
             throw new CredentialsAlreadyExistException("L'email è già registrata");
        }
        
        if (req.getTelefono() != null && !req.getTelefono().isEmpty() && clienteRepository.findByTelefono(req.getTelefono()).isPresent()) {
            throw new CredentialsAlreadyExistException("Il numero di telefono è già registrato");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(req.getNome());
        cliente.setCognome(req.getCognome());
        cliente.setEmail(req.getEmail());
        cliente.setTelefono(req.getTelefono());
        // L'indirizzo viene solitamente impostato in fase di ordine o update profilo, qui lo lasciamo null o vuoto
        
        Cliente salvato = clienteRepository.save(cliente);
        return toDTO(salvato);
    }

    public ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getCognome(), cliente.getEmail(), cliente.getTelefono());
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> getAllClienti() {
        return clienteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteDTO getCliente(Long id) {
        return clienteRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> getClientiByNome(String nome) {
        return clienteRepository.findByNome(nome)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> getClientiByCognome(String cognome) {
        return clienteRepository.findByCognome(cognome)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> getClientiByNomeAndCognome(String nome, String cognome) {
        return clienteRepository.findByNomeAndCognome(nome, cognome)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException("Cliente non trovato con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Cliente getClienteByEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato con email: " + email));
    }

    @Transactional(readOnly = true)
    public Cliente getClienteByTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato con telefono: " + telefono));
    }

    @Transactional
    public void saveCliente(Cliente c) {
        clienteRepository.save(c);
    }

    @Transactional
    public ClienteDTO updateCliente(String email, ClienteUpdateRequest request) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato"));

        if (request.getNome() != null && !request.getNome().isBlank()) {
            cliente.setNome(request.getNome());
        }
        if (request.getCognome() != null && !request.getCognome().isBlank()) {
            cliente.setCognome(request.getCognome());
        }
        if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
            // Opzionale: Controllo se il nuovo telefono è già usato da altri
            if (!cliente.getTelefono().equals(request.getTelefono()) &&
                    clienteRepository.findByTelefono(request.getTelefono()).isPresent()) {
                throw new CredentialsAlreadyExistException("Numero di telefono già in uso da un altro utente.");
            }
            cliente.setTelefono(request.getTelefono());
        }
        if (request.getIndirizzo() != null && !request.getIndirizzo().isBlank()) {
            cliente.setIndirizzo(request.getIndirizzo());
        }

        Cliente aggiornato = clienteRepository.save(cliente);
        return toDTO(aggiornato);
    }
}