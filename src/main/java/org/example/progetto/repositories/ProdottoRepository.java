package org.example.progetto.repositories;

import org.example.progetto.entities.Prodotto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto,Integer> {
    List<Prodotto> findByMarca(String marca);

    Page<Prodotto> findByMarca(String marca, Pageable pageable);

    List<Prodotto> findByCategoria(String categoria);

    Page<Prodotto> findByCategoria(String categoria, Pageable paging);

    List<Prodotto> findByColore(String colore);

    Page<Prodotto> findByColore(String colore, Pageable paging);

    List<Prodotto> findByTaglia(String taglia);

    Page<Prodotto> findByTaglia(String taglia, Pageable paging);

    List<Prodotto> findByNome(String nome);

    Page<Prodotto> findByNome(String nome, Pageable pageable);

    boolean existsById(Long id);
}
