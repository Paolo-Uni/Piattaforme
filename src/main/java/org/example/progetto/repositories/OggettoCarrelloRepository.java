package org.example.progetto.repositories;

import org.example.progetto.entities.Carrello;
import org.example.progetto.entities.OggettoCarrello;
import org.example.progetto.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OggettoCarrelloRepository extends JpaRepository<OggettoCarrello,Long> {
    OggettoCarrello findByCarrelloAndProdotto(Carrello carrello, Prodotto prod);

    Set<OggettoCarrello> findByCarrello(Carrello carrello);

    @Modifying
    @Query("delete from OggettoCarrello o where o.carrello = ?1")
    void deleteAllByCarrello(Carrello carrello);
}
