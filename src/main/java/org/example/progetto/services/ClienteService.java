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
        if (clienteRepository.existsByEmailAndTelefono(req.getEmail(), req.getTelefono())) {
            throw new CredentialsAlredyExistException("L'utente è già registrato");
        }
        Cliente cliente = new Cliente();
        cliente.setNome(req.getNome());
        cliente.setEmail(req.getEmail());
        cliente.setCognome(req.getCognome());
        Cliente salvato = clienteRepository.save(cliente);
        return toDTO(salvato);
    }

    public ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getTelefono());
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> getAllClienti() {
        return clienteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteDTO getCliente(Long id) {
        Cliente cliente =  clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato con id: " + id));
        return toDTO(cliente);
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
        return clienteRepository.findByNomeAndCognome(nome,cognome)
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
        return clienteRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Cliente getClienteByTelefono(String email) {
        return clienteRepository.findByTelefono(email);
    }

    @Transactional
    public void saveCliente(Cliente c){
        clienteRepository.save(c);
    }


}
