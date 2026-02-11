package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.progetto.dto.OggettoCarrelloDTO;
import org.example.progetto.entities.*;
import org.example.progetto.exceptions.*;
import org.example.progetto.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CarrelloService {

    @Autowired private CarrelloRepository carrelloRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ProdottoRepository prodottoRepository;
    @Autowired private OrdineRepository ordineRepository;
    @Autowired private TransazioneRepository transazioneRepository;
    @Autowired private SpedizioneRepository spedizioneRepository;
    @Autowired private OggettoCarrelloRepository oggettoCarrelloRepository;
    @Autowired private OggettoOrdineRepository oggettoOrdineRepository;
    @Autowired private EntityManager entityManager;
    
    private static final Random RANDOM = new Random();

    @Transactional
    public void aggiungiAlCarrello(String email, Long idProdotto, int quantita) throws ClienteNotFoundException, ProductNotFoundException, InvalidQuantityException {
        Cliente cliente = clienteRepository.findByEmail(email).get();
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        // Recupero o creazione carrello (Gestione Optional se aggiornato il repo)
        Carrello carrello = carrelloRepository.findByCliente(cliente).get();
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setCliente(cliente);
            carrello.setTotaleCarrello(BigDecimal.ZERO);
            carrello = carrelloRepository.save(carrello);
        } else {
            entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
        }

        Prodotto prod = prodottoRepository.findById(idProdotto)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));

        if (quantita > prod.getStock()) {
            throw new InvalidQuantityException("Quantità non disponibile");
        }

        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prod);
        if (oggetto != null) {
            entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
            int nuovaQuantita = oggetto.getQuantita() + quantita;
            if (nuovaQuantita > prod.getStock()) throw new InvalidQuantityException("Quantità totale superiore alla disponibilità");
            oggetto.setQuantita(nuovaQuantita);
            oggettoCarrelloRepository.save(oggetto);
        } else {
            OggettoCarrello aggiunta = new OggettoCarrello();
            aggiunta.setProdotto(prod);
            aggiunta.setQuantita(quantita);
            aggiunta.setCarrello(carrello);
            oggettoCarrelloRepository.save(aggiunta);
        }

        aggiornaTotaleCarrello(carrello);
    }

    @Transactional
    public void incrementaQuantitaOggettoCarrello(String email, Long idProdotto) {
        Cliente cliente = clienteRepository.findByEmail(email).get();
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente).get();
        if (carrello == null) throw new InvalidCartOperationException("Carrello non trovato.");
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Prodotto prod = prodottoRepository.findById(idProdotto).orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prod);
        if (oggetto == null) throw new ProductNotFoundException("Prodotto non nel carrello.");
        
        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
        if (oggetto.getQuantita() + 1 > prod.getStock()) throw new InvalidQuantityException("Quantità non disponibile");
        
        oggetto.setQuantita(oggetto.getQuantita() + 1);
        oggettoCarrelloRepository.save(oggetto);
        aggiornaTotaleCarrello(carrello);
    }

    @Transactional
    public void decrementaQuantitaOggettoCarrello(String email, Long idProdotto) {
        Cliente cliente = clienteRepository.findByEmail(email).get();
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente).get();
        if (carrello == null) throw new InvalidCartOperationException("Carrello non trovato.");
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Prodotto prodotto = prodottoRepository.findById(idProdotto).orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (oggetto == null) throw new InvalidCartOperationException("Prodotto non nel carrello.");
        
        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
        if (oggetto.getQuantita() > 1) {
            oggetto.setQuantita(oggetto.getQuantita() - 1);
            oggettoCarrelloRepository.save(oggetto);
        } else {
            oggettoCarrelloRepository.delete(oggetto);
        }
        aggiornaTotaleCarrello(carrello);
    }

    @Transactional
    public void rimuoviDalCarrello(String email, Long prodottoID) {
        Cliente cliente = clienteRepository.findByEmail(email).get();
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente).get();
        if (carrello == null) throw new InvalidCartOperationException("Carrello non trovato.");
        
        Prodotto prodotto = prodottoRepository.findById(prodottoID).orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (oggetto == null) throw new InvalidCartOperationException("Prodotto non nel carrello.");
        
        oggettoCarrelloRepository.delete(oggetto);
        aggiornaTotaleCarrello(carrello);
    }

    @Transactional
    public void svuotaCarrello(String email) {
        Cliente cliente = clienteRepository.findByEmail(email).get();
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente).get();
        if (carrello != null) {
            oggettoCarrelloRepository.deleteAllByCarrello(carrello);
            carrello.setTotaleCarrello(BigDecimal.ZERO);
            carrelloRepository.save(carrello);
        }
    }

    @Transactional
    public void aggiornaTotaleCarrello(Carrello carrello) {
        Set<OggettoCarrello> oggetti = oggettoCarrelloRepository.findByCarrello(carrello);
        BigDecimal totale = BigDecimal.ZERO;
        for (OggettoCarrello oc : oggetti) {
            BigDecimal riga = oc.getProdotto().getPrezzo().multiply(BigDecimal.valueOf(oc.getQuantita()));
            totale = totale.add(riga);
        }
        carrello.setTotaleCarrello(totale);
        carrelloRepository.save(carrello);
    }

    @Transactional
    public void ordina(String email, String indirizzoSpedizione) {
        Cliente cliente = clienteRepository.findByEmail(email).get();
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente).get();
        if (carrello == null) throw new InvalidCartOperationException("Carrello non trovato.");
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Set<OggettoCarrello> prodottiCarrello = oggettoCarrelloRepository.findByCarrello(carrello);
        if (prodottiCarrello.isEmpty()) throw new InvalidCartOperationException("Carrello vuoto.");

        // Validazione Stock e Lock
        for (OggettoCarrello oc : prodottiCarrello) {
            Prodotto p = oc.getProdotto();
            entityManager.lock(p, LockModeType.PESSIMISTIC_WRITE);
            if (p.getStock() < oc.getQuantita()) throw new InvalidQuantityException("Stock insufficiente per: " + p.getNome());
            p.setStock(p.getStock() - oc.getQuantita());
            prodottoRepository.save(p);
        }

        BigDecimal totaleOrdine = carrello.getTotaleCarrello();

        Ordine ordine = new Ordine();
        ordine.setCliente(cliente);
        ordine.setDataOrdine(LocalDateTime.now());
        ordine.setStato("In attesa di pagamento");
        ordine.setTotale(totaleOrdine);
        ordine = ordineRepository.save(ordine);

        Transazione transazione = new Transazione();
        transazione.setOrdine(ordine);
        transazione.setData(Instant.now());
        transazione.setImporto(totaleOrdine);

        if (processaPagamento(totaleOrdine)) {
            transazione.setEsito(true);
            ordine.setStato("Pagamento completato");
            
            // Trasferimento oggetti da Carrello a Ordine
            for (OggettoCarrello oc : prodottiCarrello) {
                OggettoOrdine oo = new OggettoOrdine();
                oo.setNomeProdotto(oc.getProdotto().getNome());
                oo.setTaglia(oc.getProdotto().getTaglia());
                oo.setColore(oc.getProdotto().getColore());
                oo.setPrezzo(oc.getProdotto().getPrezzo());
                oo.setQuantita(oc.getQuantita());
                oo.setOrdine(ordine);
                oggettoOrdineRepository.save(oo);
            }

            Spedizione spedizione = new Spedizione();
            spedizione.setOrdine(ordine);
            spedizione.setIndirizzoSpedizione(indirizzoSpedizione);
            spedizione.setDataPrevista(Instant.now().plus(7, ChronoUnit.DAYS));
            spedizione.setStato("In preparazione");
            spedizioneRepository.save(spedizione);

            // Svuoto il carrello dopo il successo
            oggettoCarrelloRepository.deleteAllByCarrello(carrello);
            carrello.setTotaleCarrello(BigDecimal.ZERO);
            carrelloRepository.save(carrello);
        } else {
            transazione.setEsito(false);
            ordine.setStato("Pagamento fallito");
            // Ricarico stock (Rollback manuale stock)
            for (OggettoCarrello oc : prodottiCarrello) {
                Prodotto p = oc.getProdotto();
                p.setStock(p.getStock() + oc.getQuantita());
                prodottoRepository.save(p);
            }
            transazioneRepository.save(transazione);
            ordineRepository.save(ordine);
            throw new PaymentException("Il pagamento è stato rifiutato.");
        }
        transazioneRepository.save(transazione);
        ordineRepository.save(ordine);
    }

    private boolean processaPagamento(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 && RANDOM.nextInt(100) < 80;
    }

    @Transactional(readOnly = true)
    public List<OggettoCarrelloDTO> getCartItemsByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email).get();
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");
        Carrello carrello = carrelloRepository.findByCliente(cliente).get();
        if (carrello == null) return new ArrayList<>();

        Set<OggettoCarrello> oggetti = oggettoCarrelloRepository.findByCarrello(carrello);
        List<OggettoCarrelloDTO> dtos = new ArrayList<>();
        for (OggettoCarrello oc : oggetti) {
            OggettoCarrelloDTO dto = new OggettoCarrelloDTO();
            dto.setIdProdotto(oc.getProdotto().getId());
            dto.setQuantita(oc.getQuantita());
            dtos.add(dto);
        }
        return dtos;
    }
}