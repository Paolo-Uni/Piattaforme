package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.progetto.entities.Prodotto;
import org.example.progetto.exceptions.InvalidQuantityException;
import org.example.progetto.exceptions.ProductAlreadyExistsException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.repositories.ProdottoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final EntityManager entityManager;

    private final List<String> TAGLIE_STANDARD = Arrays.asList("XS", "S", "M", "L", "XL", "XXL");

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
        return prodottoRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nome != null && !nome.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (marca != null && !marca.isEmpty()) {
                predicates.add(cb.equal(root.get("marca"), marca));
            }
            if (categoria != null && !categoria.isEmpty()) {
                predicates.add(cb.equal(root.get("categoria"), categoria));
            }
            if (colore != null && !colore.isEmpty()) {
                predicates.add(cb.equal(root.get("colore"), colore));
            }
            if (taglia != null && !taglia.isEmpty()) {
                if (taglia.equalsIgnoreCase("Altro")) {
                    predicates.add(cb.not(root.get("taglia").in(TAGLIE_STANDARD)));
                } else {
                    predicates.add(cb.equal(root.get("taglia"), taglia));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> getAllMarche() {
        return prodottoRepository.findDistinctMarche();
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategorie() {
        return prodottoRepository.findDistinctCategorie();
    }

    @Transactional(readOnly = true)
    public List<String> getAllColori() {
        return prodottoRepository.findDistinctColori();
    }

    @Transactional(readOnly = true)
    public List<String> getAllTaglie() {
        List<String> allTaglie = prodottoRepository.findDistinctTaglie();
        List<String> result = new ArrayList<>();
        boolean hasOther = false;

        for (String s : TAGLIE_STANDARD) {
            if (allTaglie.stream().anyMatch(t -> t.equalsIgnoreCase(s))) {
                result.add(s.toUpperCase());
            }
        }

        for (String t : allTaglie) {
            if (TAGLIE_STANDARD.stream().noneMatch(s -> s.equalsIgnoreCase(t))) {
                hasOther = true;
                break;
            }
        }

        if (hasOther) {
            result.add("Altro");
        }

        return result;
    }

    @Transactional
    public void aggiungiProdotto(Prodotto prodotto) throws ProductAlreadyExistsException {
        if (prodotto.getId() != null && prodottoRepository.existsById(prodotto.getId())) {
            throw new ProductAlreadyExistsException("Prodotto già esistente");
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
        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        
        // Lock per evitare race conditions se più admin modificano lo stock
        entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);
        
        prodotto.setStock(prodotto.getStock() + quantita);
        prodottoRepository.save(prodotto);
    }

    @Transactional
    public void diminuisciQuantitaProdotto(Long id, int quantita) {
        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        
        entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);
        
        if (prodotto.getStock() < quantita) throw new InvalidQuantityException("Stock insufficiente");
        prodotto.setStock(prodotto.getStock() - quantita);
        prodottoRepository.save(prodotto);
    }

    @Transactional(readOnly = true)
    public Prodotto getProdottoById(Long id) {
        return prodottoRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
    }
}