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
        // Correzione: Controllo separato o in OR per email e telefono dato che sono entrambi unici
        if (clienteRepository.existsByEmail(req.getEmail()) || req.getTelefono() != null) {
            throw new CredentialsAlredyExistException("L'email o il numero di telefono sono già registrati");
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
        // Utilizzo di Optional (se il repository è stato aggiornato) o gestione sicura
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
        return clienteRepository.findByEmail(email).get();
    }

    @Transactional(readOnly = true)
    public Cliente getClienteByTelefono(String telefono) {
        // Correzione: Nome del parametro corretto (era email)
        return clienteRepository.findByTelefono(telefono).get();
    }

    @Transactional
    public void saveCliente(Cliente c) {
        clienteRepository.save(c);
    }
}