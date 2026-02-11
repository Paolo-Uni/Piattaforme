package org.example.progetto.services;

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

    // ... I metodi di ricerca (ByNome, ByMarca, ecc.) funzionano già bene perché ritornano Liste ...

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByNome(String nome) {
        return prodottoRepository.findByNome(nome);
    }
    
    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByNome(String nome, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByNome(nome, paging);
        return prodotti.hasContent() ? prodotti.getContent() : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByMarca(String marca) {
        return prodottoRepository.findByMarca(marca);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByMarca(String marca, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByMarca(marca, paging);
        return prodotti.hasContent() ? prodotti.getContent() : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByCategoria(String categoria) {
        return prodottoRepository.findByCategoria(categoria);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByCategoria(String categoria, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByCategoria(categoria, paging);
        return prodotti.hasContent() ? prodotti.getContent() : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByColore(String colore) {
        return prodottoRepository.findByColore(colore);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByColore(String colore, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByColore(colore, paging);
        return prodotti.hasContent() ? prodotti.getContent() : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByTaglia(String taglia) {
        return prodottoRepository.findByTaglia(taglia);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByTaglia(String taglia, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByTaglia(taglia, paging);
        return prodotti.hasContent() ? prodotti.getContent() : new ArrayList<>();
    }

    @Transactional
    public void aggiungiProdotto(Prodotto prodotto) throws ProductAlreadyExistsException {
        // FIX: Rimosso cast a int, e controllo null safety su ID
        if(prodotto.getId() != null && prodotto.getId() != 0 && prodottoRepository.existsById(prodotto.getId()))
            throw new ProductAlreadyExistsException("Prodotto già esistente");
        if(prodotto.getStock() <= 0)
            throw new InvalidQuantityException("La quantità inserita del prodotto non è valida");
        prodottoRepository.save(prodotto);
    }

    @Transactional
    public void cancellaProdotto(Long idProdotto) {
        // FIX: Rimosso cast a int
        if(idProdotto != null && idProdotto != 0 && prodottoRepository.existsById(idProdotto))
            prodottoRepository.delete(prodottoRepository.findById(idProdotto).get());
        else
            throw new ProductNotFoundException("Prodotto non trovato");
    }

    @Transactional
    public void aumentaQuantitaProdotto(Long id, int quantita){
        // FIX: Uso findById().orElseThrow() per gestire l'Optional
        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        
        prodotto.setStock(prodotto.getStock() + quantita);
        prodottoRepository.save(prodotto);
    }

    @Transactional
    public void diminuisciQuantitaProdotto(Long id, int quantita){
        // FIX: Uso findById().orElseThrow()
        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        
        if(prodotto.getStock() - quantita < 0)
            throw new InvalidQuantityException("Quantità invalida");
        
        prodotto.setStock(prodotto.getStock() - quantita);
        prodottoRepository.save(prodotto);
    }
}