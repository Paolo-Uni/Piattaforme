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

    // FIX: cambiato idProdotto da int a Long
    @Transactional
    public void aggiungiAlCarrello(String email, Long idProdotto, int quantita) throws ClienteNotFoundException, ProductNotFoundException, InvalidQuantityException {

        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente((cliente));
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setCliente(cliente);
            carrello = carrelloRepository.save(carrello);
        } else {
            entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
        }

        // FIX: Trova direttamente il prodotto invece di scaricarli tutti (stream rimosso)
        Prodotto prod = prodottoRepository.findById(idProdotto)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto con ID " + idProdotto + " non trovato"));

        if (quantita > prod.getStock()) {
            throw new InvalidQuantityException("Quantità non disponibile");
        }

        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prod);
        if (oggetto != null) {
            entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
            int nuovaQuantita = oggetto.getQuantita() + quantita;
            if (nuovaQuantita <= prod.getStock()) {
                oggetto.setQuantita(nuovaQuantita);
                oggettoCarrelloRepository.save(oggetto);
            } else {
                throw new InvalidQuantityException("Quantità totale superiore alla disponibilità");
            }
        } else {
            OggettoCarrello aggiunta = new OggettoCarrello();
            aggiunta.setProdotto(prod);
            aggiunta.setQuantita(quantita);
            aggiunta.setCarrello(carrello);
            oggettoCarrelloRepository.save(aggiunta);
        }
    }

    @Transactional
    public void incrementaQuantitaOggettoCarrello(String email, Long idProdotto) throws ClienteNotFoundException, ProductNotFoundException, InvalidQuantityException {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) throw new InvalidCartOperationException("Carrello non trovato.");
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Prodotto prod = prodottoRepository.findById(idProdotto)
                 .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));

        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prod);
        if (oggetto == null) throw new ProductNotFoundException("Prodotto non nel carrello.");
        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);

        if (oggetto.getQuantita() + 1 > prod.getStock()) {
            throw new InvalidQuantityException("Quantità non disponibile");
        }
        oggetto.setQuantita(oggetto.getQuantita() + 1);
        oggettoCarrelloRepository.save(oggetto);
    }

    @Transactional
    public void rimuoviDalCarrello(String email, Long prodottoID) throws ClienteNotFoundException, InvalidCartOperationException {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) throw new InvalidCartOperationException("Carrello non trovato.");
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Prodotto prodotto = prodottoRepository.findById(prodottoID)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
                
        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (oggetto == null) throw new InvalidCartOperationException("Prodotto non nel carrello.");
        
        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
        oggettoCarrelloRepository.delete(oggetto);
    }

    @Transactional
    public void decrementaQuantitaOggettoCarrello(String email, Long idProdotto) throws ClienteNotFoundException, InvalidCartOperationException {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) throw new InvalidCartOperationException("Carrello non trovato.");
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Prodotto prodotto = prodottoRepository.findById(idProdotto)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));

        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (oggetto == null) throw new InvalidCartOperationException("Prodotto non nel carrello.");
        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);

        if (oggetto.getQuantita() > 1) {
            oggetto.setQuantita(oggetto.getQuantita() - 1);
            oggettoCarrelloRepository.save(oggetto);
        } else {
            oggettoCarrelloRepository.delete(oggetto);
        }
    }

    @Transactional
    public void svuotaCarrello(String email) throws ClienteNotFoundException {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello != null) {
            entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
            oggettoCarrelloRepository.deleteAllByCarrello(carrello);
        }
    }

    @Transactional
    public void ordina(String email, String indirizzoSpedizione)
            throws ClienteNotFoundException, InvalidCartOperationException, InvalidQuantityException {

        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) throw new InvalidCartOperationException("Carrello non trovato.");
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Set<OggettoCarrello> prodottiCliente = oggettoCarrelloRepository.findByCarrello(carrello);
        if (prodottiCliente.isEmpty()) {
            throw new InvalidCartOperationException("Carrello vuoto.");
        }

        List<OggettoCarrello> oggetti = new ArrayList<>(prodottiCliente);
        oggetti.sort(Comparator.comparingLong(OggettoCarrello::getId));

        for (OggettoCarrello oggetto : oggetti) {
            entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
            Prodotto prodotto = prodottoRepository.findById(oggetto.getProdotto().getId())
                    .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));
            entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);

            if (prodotto.getStock() < oggetto.getQuantita()) {
                throw new InvalidQuantityException("Quantità non sufficiente per: " + prodotto.getNome());
            }
            prodotto.setStock(prodotto.getStock() - oggetto.getQuantita());
            prodottoRepository.save(prodotto);
        }

        Ordine ordine = new Ordine();
        ordine.setCliente(cliente);
        ordine.setDataOrdine(LocalDateTime.now());
        ordine.setStato("Elaborazione...");
        ordineRepository.save(ordine);

        Transazione transazione = new Transazione();
        transazione.setOrdine(ordine);
        transazione.setData(Instant.now());
        BigDecimal importo = calcolaImporto(prodottiCliente);
        transazione.setImporto(importo);

        Spedizione spedizione = new Spedizione();
        spedizione.setOrdine(ordine);
        spedizione.setIndirizzoSpedizione(indirizzoSpedizione);
        spedizione.setDataPrevista(Instant.now().plus(7, ChronoUnit.DAYS));
        spedizione.setStato("In corso...");

        boolean esitoPagamento = processaPagamento(transazione.getImporto());

        if (esitoPagamento) {
            transazione.setEsito(true);
            ordine.setStato("Pagamento completato");
            oggettoCarrelloRepository.deleteAllByCarrello(carrello);

            for (OggettoCarrello oc : oggetti) {
                OggettoOrdine oo = new OggettoOrdine();
                oo.setNomeProdotto(oc.getProdotto().getNome());
                oo.setTaglia(oc.getProdotto().getTaglia());
                oo.setColore(oc.getProdotto().getColore());
                oo.setDescrizione(oc.getProdotto().getDescrizione());
                oo.setPrezzo(oc.getProdotto().getPrezzo());
                oo.setQuantita(oc.getQuantita());
                oo.setOrdine(ordine);
                oggettoOrdineRepository.save(oo);
            }
            ordine.setTotale(importo);

        } else {
            transazione.setEsito(false);
            ordine.setStato("Pagamento fallito");
            
            for (OggettoCarrello oc : oggetti) {
                Prodotto prodotto = prodottoRepository.findById(oc.getProdotto().getId()).get();
                entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);
                prodotto.setStock(prodotto.getStock() + oc.getQuantita());
                prodottoRepository.save(prodotto);
            }
            
            transazioneRepository.save(transazione);
            ordineRepository.save(ordine);
            throw new PaymentException("Pagamento fallito.");
        }

        transazioneRepository.save(transazione);
        spedizioneRepository.save(spedizione);
        ordineRepository.save(ordine);
    }

    private static BigDecimal calcolaImporto(Set<OggettoCarrello> prodottiUser) {
        BigDecimal totale = BigDecimal.ZERO;
        if (prodottiUser == null) return totale;
        for (OggettoCarrello oggetto : prodottiUser) {
            totale = totale.add(oggetto.getProdotto().getPrezzo().multiply(BigDecimal.valueOf(oggetto.getQuantita())));
        }
        return totale;
    }

    private boolean processaPagamento(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        return RANDOM.nextInt(100) < 80;
    }

    @Transactional(readOnly = true)
    public List<OggettoCarrelloDTO> getCartItemsByEmail(String email) throws ClienteNotFoundException {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) throw new ClienteNotFoundException("Cliente non trovato!");

        Carrello carrello = carrelloRepository.findByCliente(cliente);
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