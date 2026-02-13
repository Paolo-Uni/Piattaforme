package org.example.progetto.repositories;

import org.example.progetto.entities.Ordine;
import org.example.progetto.entities.Spedizione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpedizioneRepository extends JpaRepository<Spedizione, Long> {
    Optional<Spedizione> findByOrdine(Ordine ordine);
}