package org.example.progetto.repositories;

import org.example.progetto.entities.Spedizione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpedizioneRepository extends JpaRepository<Spedizione, Long> {
}