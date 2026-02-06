package org.example.progetto.repositories;

import org.example.progetto.entities.Transazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransazioneRepository extends JpaRepository<Transazione, Long> {
}
