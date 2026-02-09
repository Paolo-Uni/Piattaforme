package org.example.progetto.repositories;

import org.example.progetto.entities.Carrello;
import org.example.progetto.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello,Long> {
    Carrello findByCliente(Cliente cliente);
}
