package org.example.progetto.repositories;

import org.example.progetto.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {

    List<Cliente> findByNome(String nome);

    List<Cliente> findByCognome(String cognome);

    List<Cliente> findByNomeAndCognome(String nome, String cognome);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByTelefono(String telefono);

    boolean existsByEmail(String email);
}
