package org.example.progetto.services;

import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.ClienteDTO;
import org.example.progetto.dto.ClienteUpdateRequest;
import org.example.progetto.dto.Request;
import org.example.progetto.entities.Cliente;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.CredentialsAlreadyExistException;
import org.example.progetto.exceptions.InvalidOperationException;
import org.example.progetto.repositories.CarrelloRepository;
import org.example.progetto.repositories.ClienteRepository;
import org.example.progetto.repositories.OggettoCarrelloRepository;
import org.example.progetto.repositories.OrdineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final OrdineRepository ordineRepository;
    private final CarrelloRepository carrelloRepository;
    private final OggettoCarrelloRepository oggettoCarrelloRepository;

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
        
        Cliente salvato = clienteRepository.save(cliente);
        return toDTO(salvato);
    }

    public ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
            cliente.getId(), 
            cliente.getNome(), 
            cliente.getCognome(), 
            cliente.getEmail(), 
            cliente.getTelefono(),
            cliente.getIndirizzo()
        );
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

    // ... Metodi di ricerca invariati ...
    @Transactional(readOnly = true)
    public List<ClienteDTO> getClientiByNome(String nome) {
        return clienteRepository.findByNome(nome).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> getClientiByCognome(String cognome) {
        return clienteRepository.findByCognome(cognome).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> getClientiByNomeAndCognome(String nome, String cognome) {
        return clienteRepository.findByNomeAndCognome(nome, cognome).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato con ID: " + id));

        // Controllo ordini: Se ha ordini, impediamo la cancellazione per integrità dati
        if (ordineRepository.existsByCliente(cliente)) {
            throw new InvalidOperationException("Impossibile eliminare un cliente che ha già effettuato ordini.");
        }

        // Pulizia Carrello: Se ha un carrello, lo svuotiamo e cancelliamo
        carrelloRepository.findByCliente(cliente).ifPresent(c -> {
            oggettoCarrelloRepository.deleteAllByCarrello(c);
            carrelloRepository.delete(c);
        });

        clienteRepository.delete(cliente);
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
        
        // FIX: Gestione corretta telefono (evita NullPointerException e permette rimozione)
        if (request.getTelefono() != null) {
            if (request.getTelefono().isBlank()) {
                cliente.setTelefono(null);
            } else {
                String newPhone = request.getTelefono();
                // Verifica duplicati solo se il numero è diverso da quello attuale
                if (!Objects.equals(cliente.getTelefono(), newPhone) && 
                    clienteRepository.findByTelefono(newPhone).isPresent()) {
                    throw new CredentialsAlreadyExistException("Numero di telefono già in uso da un altro utente.");
                }
                cliente.setTelefono(newPhone);
            }
        }

        // FIX: Gestione indirizzo (permette rimozione)
        if (request.getIndirizzo() != null) {
            if (request.getIndirizzo().isBlank()) {
                cliente.setIndirizzo(null);
            } else {
                cliente.setIndirizzo(request.getIndirizzo());
            }
        }

        Cliente aggiornato = clienteRepository.save(cliente);
        return toDTO(aggiornato);
    }
}