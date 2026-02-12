package org.example.progetto.repositories;

import org.example.progetto.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {

    @Query("SELECT DISTINCT p.marca FROM Prodotto p WHERE p.marca IS NOT NULL")
    List<String> findDistinctMarche();

    @Query("SELECT DISTINCT p.categoria FROM Prodotto p WHERE p.categoria IS NOT NULL")
    List<String> findDistinctCategorie();

    @Query("SELECT DISTINCT p.taglia FROM Prodotto p WHERE p.taglia IS NOT NULL")
    List<String> findDistinctTaglie();

    @Query("SELECT DISTINCT p.colore FROM Prodotto p WHERE p.colore IS NOT NULL")
    List<String> findDistinctColori();
    
    // Serve per supportare la Specification nel service
    List<Prodotto> findAll(org.springframework.data.jpa.domain.Specification<Prodotto> spec);
    org.springframework.data.domain.Page<Prodotto> findAll(org.springframework.data.jpa.domain.Specification<Prodotto> spec, org.springframework.data.domain.Pageable pageable);
}