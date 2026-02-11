package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.progetto.dto.OggettoOrdineDTO;
import org.example.progetto.dto.OrdineDTO;
import org.example.progetto.entities.*;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.InvalidOperationException;
import org.example.progetto.repositories.ClienteRepository;
import org.example.progetto.repositories.OggettoOrdineRepository;
import org.example.progetto.repositories.OrdineRepository;
import org.example.progetto.support.StatoOrdine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class OrdineService {

    private static final Random RANDOM = new Random();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OggettoOrdineRepository oggettoOrdineRepository;

    @Transactional
    public void annullaOrdine(Cliente cDetached, Ordine oDetached, String motivo) {
        Cliente c = clienteRepository.findById(cDetached.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato"));
        
        entityManager.lock(c, LockModeType.PESSIMISTIC_WRITE);

        Ordine o = ordineRepository.findById(oDetached.getId())
                .orElseThrow(() -> new InvalidOperationException("Ordine non trovato"));
        
        entityManager.lock(o, LockModeType.PESSIMISTIC_WRITE);

        if (!o.getCliente().getId().equals(c.getId())) {
            throw new InvalidOperationException("L'ordine non appartiene all'utente specificato.");
        }

        // UPDATE: Controllo tramite Enum
        StatoOrdine statoAttuale = o.getStato();
        if (statoAttuale == StatoOrdine.ANNULLATO || statoAttuale == StatoOrdine.SPEDITO) {
            throw new InvalidOperationException("Impossibile annullare l'ordine nello stato attuale: " + statoAttuale);
        }

        Spedizione sped = o.getSpedizione();
        if (sped != null) {
            entityManager.lock(sped, LockModeType.PESSIMISTIC_WRITE);
            sped.setStato("Annullata a seguito di cancellazione ordine");
        }

        // UPDATE: Setto stato Enum e salvo il motivo nelle note
        o.setStato(StatoOrdine.ANNULLATO);
        o.setNote("Motivo annullamento: " + motivo);
        ordineRepository.save(o);

        try {
            processaRimborso(o);
        } catch (Exception e) {
            System.err.println("Fallimento rimborso per ordine " + o.getId() + ": " + e.getMessage());
            // UPDATE: Aggiorno le note per segnalare l'errore
            o.setNote(o.getNote() + " | ERRORE: Rimborso FALLITO, contattare assistenza.");
            ordineRepository.save(o);
        }
    }

    private void processaRimborso(Ordine ordine) {
        BigDecimal importoRimborso = calcolaImportoRimborso(ordine);
        simulaRimborso(importoRimborso);
    }

    private BigDecimal calcolaImportoRimborso(Ordine ordine) {
        Transazione transazione = ordine.getTransazione();
        if (transazione != null && transazione.isEsito()) {
            entityManager.lock(transazione, LockModeType.PESSIMISTIC_WRITE);
            return transazione.getImporto();
        } else {
            throw new InvalidOperationException("Nessuna transazione completata trovata per questo ordine.");
        }
    }

    private void simulaRimborso(BigDecimal importo) {
        System.out.println("Richiesta rimborso inviata al gateway. Importo: " + importo);
        boolean rimborsoSuccesso = RANDOM.nextInt(100) < 90;

        if (rimborsoSuccesso) {
            System.out.println("Rimborso confermato dal gateway.");
        } else {
            throw new InvalidOperationException("Il gateway di pagamento ha rifiutato la richiesta di rimborso.");
        }
    }

    @Transactional(readOnly = true)
    public List<OrdineDTO> getOrdiniByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato con email: " + email));

        List<Ordine> ordini = ordineRepository.findByCliente(cliente);
        List<OrdineDTO> ordiniDTO = new ArrayList<>();

        for (Ordine ordine : ordini) {
            OrdineDTO dto = new OrdineDTO();
            dto.setIdOrdine(ordine.getId());
            dto.setData(ordine.getDataOrdine());
            // UPDATE: Converto l'Enum in Stringa per il DTO
            dto.setStato(ordine.getStato().toString()); 
            dto.setTotaleOrdine(ordine.getTotale());

            List<OggettoOrdine> prodottiOrdinati = oggettoOrdineRepository.findByOrdine(ordine);
            List<OggettoOrdineDTO> prodottiDTO = new ArrayList<>();

            for (OggettoOrdine po : prodottiOrdinati) {
                OggettoOrdineDTO oggettoDTO = new OggettoOrdineDTO();
                oggettoDTO.setIdOggetto(po.getId());
                oggettoDTO.setNome(po.getNomeProdotto());
                oggettoDTO.setTaglia(po.getTaglia());
                oggettoDTO.setColore(po.getColore());
                oggettoDTO.setPrezzo(po.getPrezzo());
                oggettoDTO.setQuantita(po.getQuantita());
                prodottiDTO.add(oggettoDTO);
            }

            dto.setOggetti(prodottiDTO);
            ordiniDTO.add(dto);
        }
        return ordiniDTO;
    }

    @Transactional(readOnly = true)
    public Ordine getOrdineById(Long idOrdine) {
        return ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new InvalidOperationException("Ordine non trovato con ID: " + idOrdine));
    }
}