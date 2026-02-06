package org.example.progetto.services;

import org.example.progetto.entities.Prodotto;
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
        if(prodotti.hasContent())
            return prodotti.getContent();
        else
            return new ArrayList<>();
    }
    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByNome(String nome) {
        return prodottoRepository.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByNome(String nome, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByNome(nome, paging);
        if(prodotti.hasContent())
            return prodotti.getContent();
        else
            return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByMarca(String marca) {
        return prodottoRepository.findByMarca(marca);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByMarca(String marca, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByMarca(marca, paging);
        if(prodotti.hasContent())
            return prodotti.getContent();
        else
            return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByCategoria(String categoria) {
        return  prodottoRepository.findByCategoria(categoria);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByCategoria(String categoria, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByCategoria(categoria, paging);
        if(prodotti.hasContent())
            return prodotti.getContent();
        else
            return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByColore(String colore) {
        return prodottoRepository.findByColore(colore);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByColore(String colore, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByColore(colore, paging);
        if(prodotti.hasContent())
            return prodotti.getContent();
        else
            return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByTaglia(String taglia) {
        return prodottoRepository.findByTaglia(taglia);
    }

    @Transactional(readOnly = true)
    public List<Prodotto> getProdottiByTaglia(String taglia, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Prodotto> prodotti = prodottoRepository.findByTaglia(taglia, paging);
        if(prodotti.hasContent())
            return prodotti.getContent();
        else
            return new ArrayList<>();
    }

    @Transactional
    public void aggiungiProdotto(Prodotto prodotto) {
        if(prodotto.getId() != 0 && prodottoRepository.existsById(Math.toIntExact(prodotto.getId())))
            throw new RuntimeException("Prodotto già existente");
        prodottoRepository.save(prodotto);
    }

    @Transactional
    public void deleteProdotto(Prodotto prodotto) {
        if(prodotto.getId() != 0 && prodottoRepository.existsById(Math.toIntExact(prodotto.getId())))
            prodottoRepository.delete(prodotto);
    }

    @Transactional
    public void rifornisciProdotto(Prodotto prodotto, int quantita){
        prodotto.setStock(prodotto.getStock()+quantita);
        prodottoRepository.save(prodotto);
    }

}
