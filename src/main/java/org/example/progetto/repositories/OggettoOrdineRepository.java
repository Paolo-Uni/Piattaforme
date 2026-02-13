package org.example.progetto.repositories;

import org.example.progetto.entities.OggettoOrdine;
import org.example.progetto.entities.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OggettoOrdineRepository extends JpaRepository<OggettoOrdine, Long> {
    List<OggettoOrdine> findByOrdine(Ordine ordine);
}