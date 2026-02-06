package org.example.progetto.repositories;

import org.example.progetto.entities.Carrello;
import org.example.progetto.entities.OggettoCarrello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OggettoCarrelloRepository extends JpaRepository<OggettoCarrello,Long> {
}
