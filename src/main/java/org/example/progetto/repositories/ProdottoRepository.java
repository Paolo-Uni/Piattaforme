package org.example.progetto.repositories;

import org.example.progetto.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
// FIX: Cambiato Integer -> Long per corrispondere all'entità Prodotto
public interface ProdottoRepository extends JpaRepository<Prodotto, Long>, JpaSpecificationExecutor<Prodotto> {

}