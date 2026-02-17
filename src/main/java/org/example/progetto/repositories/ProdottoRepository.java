package org.example.progetto.repositories;

import org.example.progetto.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, Long>, JpaSpecificationExecutor<Prodotto> {

    @Query("SELECT DISTINCT p.marca FROM Prodotto p")
    List<String> findDistinctMarche();

    @Query("SELECT DISTINCT p.categoria FROM Prodotto p")
    List<String> findDistinctCategorie();

    @Query("SELECT DISTINCT p.colore FROM Prodotto p")
    List<String> findDistinctColori();

    @Query("SELECT DISTINCT p.taglia FROM Prodotto p")
    List<String> findDistinctTaglie();
}