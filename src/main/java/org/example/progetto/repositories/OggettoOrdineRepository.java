package org.example.progetto.repositories;

import org.example.progetto.entities.OggettoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OggettoOrdineRepository extends JpaRepository<OggettoOrdine,Long> {
}
