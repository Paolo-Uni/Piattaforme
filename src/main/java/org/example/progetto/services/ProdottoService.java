package org.example.progetto.services;

import jakarta.persistence.criteria.Predicate;
import org.example.progetto.entities.Prodotto;
import org.example.progetto.exceptions.InvalidQuantityException;
import org.example.progetto.exceptions.ProductAlreadyExistsException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.repositories.ProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdottoService {

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Transactional(readOnly = true)
    public List<Prodotto> getProdotti() {
        return prodottoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdotti(int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findAll(paging);
        return prodotti.hasContent() ? prodotti.getContent() : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public Page<Prodotto> ricercaDinamica(String nome, String marca, String categoria, String colore, String taglia, Pageable pageable) {
        // Nota: "_" è valido in Java 21+ con --enable-preview abilitato nel pom.xml
        return prodottoRepository.findAll((Specification<Prodotto>) (root, _, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nome != null && !nome.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (marca != null && !marca.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("marca"), marca));
            }
            if (categoria != null && !categoria.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("categoria"), categoria));
            }
            if (colore != null && !colore.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("colore"), colore));
            }
            if (taglia != null && !taglia.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("taglia"), taglia));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional
    public void aggiungiProdotto(Prodotto prodotto) throws ProductAlreadyExistsException {
        if (prodotto.getId() != null && prodottoRepository.existsById(prodotto.getId())) {
            throw new ProductAlreadyExistsException("Prodotto già esistente con ID: " + prodotto.getId());
        }
        
        if (prodotto.getStock() == null || prodotto.getStock() < 0) {
            throw new InvalidQuantityException("La quantità inserita non è valida");
        }
        
        prodottoRepository.save(prodotto);
    }

    @Transactional
    public void cancellaProdotto(Long idProdotto) {
        if (!prodottoRepository.existsById(idProdotto)) {
            throw new ProductNotFoundException("Prodotto non trovato");
        }
        prodottoRepository.deleteById(idProdotto);
    }

    @Transactional
    public void aumentaQuantitaProdotto(Long id, int quantita) {
        if (quantita <= 0) {
            throw new InvalidQuantityException("La quantità da aggiungere deve essere positiva");
        }
        
        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        
        prodotto.setStock(prodotto.getStock() + quantita);
        prodottoRepository.save(prodotto);
    }

    @Transactional
    public void diminuisciQuantitaProdotto(Long id, int quantita) {
        if (quantita <= 0) {
            throw new InvalidQuantityException("La quantità da sottrarre deve essere positiva");
        }

        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        
        if (prodotto.getStock() - quantita < 0) {
            throw new InvalidQuantityException("Quantità in stock insufficiente");
        }
        
        prodotto.setStock(prodotto.getStock() - quantita);
        prodottoRepository.save(prodotto);
    }

    // RIAGGIUNTO: Metodo utile per dettaglio prodotto o controlli futuri
    @Transactional(readOnly = true)
    public Prodotto getProdottoById(Long id) {
        return prodottoRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato con ID: " + id));
    }
}