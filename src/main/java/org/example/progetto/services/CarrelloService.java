package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.OggettoCarrelloDTO;
import org.example.progetto.entities.*;
import org.example.progetto.exceptions.*;
import org.example.progetto.repositories.*;
import org.example.progetto.support.StatoOrdine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime; // Uniformato a LocalDateTime
import java.util.*;

@Service
@RequiredArgsConstructor
public class CarrelloService {

    private final CarrelloRepository carrelloRepository;
    private final ClienteRepository clienteRepository;
    private final ProdottoRepository prodottoRepository;
    private final OrdineRepository ordineRepository;
    private final TransazioneRepository transazioneRepository;
    private final SpedizioneRepository spedizioneRepository;
    private final OggettoCarrelloRepository oggettoCarrelloRepository;
    private final OggettoOrdineRepository oggettoOrdineRepository;
    private final EntityManager entityManager;

    private static final Random RANDOM = new Random();

    @Transactional
    public void aggiungiAlCarrello(String email, Long idProdotto, int quantita) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato!"));

        Carrello carrello = carrelloRepository.findByCliente(cliente)
                .orElseGet(() -> {
                    Carrello c = new Carrello();
                    c.setCliente(cliente);
                    c.setTotaleCarrello(BigDecimal.ZERO);
                    return carrelloRepository.save(c);
                });

        entityManager.refresh(carrello);
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Prodotto prod = prodottoRepository.findById(idProdotto)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));

        if (quantita > prod.getStock()) {
            throw new InvalidQuantityException("Quantità richiesta superiore allo stock disponibile");
        }

        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prod);
        if (oggetto != null) {
            entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
            int nuovaQuantita = oggetto.getQuantita() + quantita;
            if (nuovaQuantita > prod.getStock()) {
                throw new InvalidQuantityException("Quantità totale nel carrello superiore alla disponibilità");
            }
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
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato!"));

        Carrello carrello = carrelloRepository.findByCliente(cliente)
                .orElseThrow(() -> new InvalidCartOperationException("Carrello non trovato."));

        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Prodotto prod = prodottoRepository.findById(idProdotto)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));

        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prod);
        if (oggetto == null) throw new ProductNotFoundException("Prodotto non presente nel carrello.");

        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
        if (oggetto.getQuantita() + 1 > prod.getStock()) {
            throw new InvalidQuantityException("Stock insufficiente per incrementare la quantità");
        }

        oggetto.setQuantita(oggetto.getQuantita() + 1);
        oggettoCarrelloRepository.save(oggetto);
        aggiornaTotaleCarrello(carrello);
    }

    @Transactional
    public void decrementaQuantitaOggettoCarrello(String email, Long idProdotto) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato!"));

        Carrello carrello = carrelloRepository.findByCliente(cliente)
                .orElseThrow(() -> new InvalidCartOperationException("Carrello non trovato."));

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
        aggiornaTotaleCarrello(carrello);
    }

    @Transactional
    public void rimuoviProdottoDalCarrello(String email, Long idProdotto) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato!"));

        Carrello carrello = carrelloRepository.findByCliente(cliente)
                .orElseThrow(() -> new InvalidCartOperationException("Carrello non trovato."));

        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        Prodotto prodotto = prodottoRepository.findById(idProdotto)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato"));

        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (oggetto != null) {
            oggettoCarrelloRepository.delete(oggetto);
            entityManager.flush(); 
            aggiornaTotaleCarrello(carrello);
        } else {
            throw new ProductNotFoundException("Il prodotto non è presente nel carrello.");
        }
    }

    @Transactional
    public void svuotaCarrello(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato!"));

        Carrello carrello = carrelloRepository.findByCliente(cliente).orElse(null);
        if (carrello != null) {
            oggettoCarrelloRepository.deleteAllByCarrello(carrello);
            carrello.setTotaleCarrello(BigDecimal.ZERO);
            carrelloRepository.save(carrello);
        }
    }

    @Transactional
    public void aggiornaTotaleCarrello(Carrello carrello) {
        List<OggettoCarrello> oggetti = oggettoCarrelloRepository.findByCarrello(carrello);
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
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato!"));

        Carrello carrello = carrelloRepository.findByCliente(cliente)
                .orElseThrow(() -> new InvalidCartOperationException("Carrello non trovato."));

        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        List<OggettoCarrello> prodottiCarrello = oggettoCarrelloRepository.findByCarrello(carrello);
        if (prodottiCarrello.isEmpty()) throw new InvalidCartOperationException("Carrello vuoto.");

        for (OggettoCarrello oc : prodottiCarrello) {
            Prodotto p = oc.getProdotto();
            entityManager.lock(p, LockModeType.PESSIMISTIC_WRITE);
            if (p.getStock() < oc.getQuantita()) {
                throw new InvalidQuantityException("Stock insufficiente per il prodotto: " + p.getNome());
            }
            p.setStock(p.getStock() - oc.getQuantita());
            prodottoRepository.save(p);
        }

        BigDecimal totaleOrdine = carrello.getTotaleCarrello();

        Ordine ordine = new Ordine();
        ordine.setCliente(cliente);
        ordine.setDataOrdine(LocalDateTime.now());
        ordine.setStato(StatoOrdine.IN_ATTESA_DI_PAGAMENTO);
        ordine.setTotale(totaleOrdine);
        ordine = ordineRepository.save(ordine);

        Transazione transazione = new Transazione();
        transazione.setOrdine(ordine);
        // FIX: Uso LocalDateTime per coerenza
        transazione.setData(LocalDateTime.now());
        transazione.setImporto(totaleOrdine);

        if (processaPagamento(totaleOrdine)) {
            transazione.setEsito(true);
            transazioneRepository.save(transazione);
            ordine.setStato(StatoOrdine.PAGAMENTO_COMPLETATO);

            for (OggettoCarrello oc : prodottiCarrello) {
                OggettoOrdine oo = new OggettoOrdine();
                // FIX: Salviamo l'ID originale per lo storico
                oo.setProdottoId(oc.getProdotto().getId()); 
                oo.setNomeProdotto(oc.getProdotto().getNome());
                oo.setTaglia(oc.getProdotto().getTaglia());
                oo.setColore(oc.getProdotto().getColore());
                oo.setDescrizione(oc.getProdotto().getDescrizione());
                oo.setPrezzo(oc.getProdotto().getPrezzo());
                oo.setQuantita(oc.getQuantita());
                oo.setOrdine(ordine);
                oggettoOrdineRepository.save(oo);
            }

            Spedizione spedizione = new Spedizione();
            spedizione.setOrdine(ordine);
            spedizione.setIndirizzoSpedizione(indirizzoSpedizione);
            // FIX: Uso LocalDateTime
            spedizione.setDataPrevista(LocalDateTime.now().plusDays(7));
            spedizione.setStato("In preparazione");
            spedizioneRepository.save(spedizione);
            ordine.setSpedizione(spedizione);

            ordineRepository.save(ordine);

            oggettoCarrelloRepository.deleteAllByCarrello(carrello);
            carrello.setTotaleCarrello(BigDecimal.ZERO);
            carrelloRepository.save(carrello);
        } else {
            throw new PaymentException("Il pagamento è stato rifiutato.");
        }
    }

    private boolean processaPagamento(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 && RANDOM.nextInt(100) < 80;
    }

    @Transactional(readOnly = true)
    public List<OggettoCarrelloDTO> getCartItemsByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato!"));

        Carrello carrello = carrelloRepository.findByCliente(cliente).orElse(null);
        if (carrello == null) return new ArrayList<>();

        List<OggettoCarrello> oggetti = oggettoCarrelloRepository.findByCarrello(carrello);
        List<OggettoCarrelloDTO> dtos = new ArrayList<>();
        for (OggettoCarrello oc : oggetti) {
            OggettoCarrelloDTO dto = new OggettoCarrelloDTO();
            dto.setIdProdotto(oc.getProdotto().getId());
            dto.setNomeProdotto(oc.getProdotto().getNome());
            dto.setPrezzoUnitario(oc.getProdotto().getPrezzo());
            dto.setColore(oc.getProdotto().getColore());
            dto.setTaglia(oc.getProdotto().getTaglia());
            dto.setQuantita(oc.getQuantita());
            dtos.add(dto);
        }
        return dtos;
    }
}