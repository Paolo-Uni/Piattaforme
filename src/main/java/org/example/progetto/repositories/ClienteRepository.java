package org.example.progetto.repositories;

import org.example.progetto.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {

    boolean existsByEmailAndTelefono(String email, String telefono);

    List<Cliente> findByNome(String nome);

    List<Cliente> findByCognome(String cognome);

    List<Cliente> findByNomeAndCognome(String nome, String cognome);

    Cliente findByEmail(String email);

    Cliente findByTelefono(String email);
}
