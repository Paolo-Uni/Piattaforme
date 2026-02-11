package org.example.progetto.services;

import org.example.progetto.dto.ClienteDTO;
import org.example.progetto.dto.Request;
import org.example.progetto.entities.Cliente;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.CredentialsAlredyExistException;
import org.example.progetto.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public ClienteDTO registraCliente(Request req) throws CredentialsAlredyExistException {
        // CORREZIONE: Controlliamo se l'email esiste OPPURE se il telefono è fornito ED esiste già
        if (clienteRepository.existsByEmail(req.getEmail())) {
             throw new CredentialsAlredyExistException("L'email è già registrata");
        }
        
        if (req.getTelefono() != null && clienteRepository.findByTelefono(req.getTelefono()).isPresent()) {
            throw new CredentialsAlredyExistException("Il numero di telefono è già registrato");
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
}