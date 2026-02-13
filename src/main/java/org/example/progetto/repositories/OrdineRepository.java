package org.example.progetto.repositories;

import org.example.progetto.entities.Cliente;
import org.example.progetto.entities.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long> {
    List<Ordine> findByCliente(Cliente cliente);
}