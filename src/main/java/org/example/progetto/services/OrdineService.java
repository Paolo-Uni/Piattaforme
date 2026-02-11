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
    public void annullaOrdine(Cliente c, Ordine o, String motivo) {
        // Verifica esistenza e applica Lock PESSIMISTIC_WRITE per evitare race conditions
        if(!clienteRepository.existsById(c.getId()))
            throw new ClienteNotFoundException("Cliente non trovato");
        entityManager.lock(c, LockModeType.PESSIMISTIC_WRITE);

        if(!ordineRepository.existsById(o.getId()))
            throw new InvalidOperationException("Ordine non trovato");
        entityManager.lock(o, LockModeType.PESSIMISTIC_WRITE);

        // Controllo appartenenza ordine
        if (!(o.getCliente().getId().equals(c.getId()))) {
            throw new InvalidOperationException("L'ordine non appartiene all'utente.");
        }

        // Controllo stato ordine: non si può annullare un ordine già spedito o annullato
        if (o.getStato().contains("Annullato") || o.getStato().contains("Spedito")) {
            throw new InvalidOperationException("Impossibile annullare l'ordine nello stato attuale: " + o.getStato());
        }

        // Gestione Spedizione
        Spedizione sped = o.getSpedizione();
        if (sped != null) {
            entityManager.lock(sped, LockModeType.PESSIMISTIC_WRITE);
            sped.setStato("Annullata a seguito di cancellazione ordine");
        }

        // Aggiornamento stato ordine
        o.setStato("Annullato. Motivo: " + motivo);
        ordineRepository.save(o);

        // Gestione Rimborso: Logica separata per non invalidare l'annullamento se il gateway fallisce
        try {
            processaRimborso(o);
        } catch (Exception e) {
            // Se il rimborso fallisce, logghiamo l'errore ma l'ordine resta annullato.
            // Lo stato viene aggiornato per indicare che il rimborso deve essere gestito manualmente.
            System.err.println("Fallimento rimborso per ordine " + o.getId() + ": " + e.getMessage());
            o.setStato(o.getStato() + " (Rimborso in sospeso)");
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
        // Probabilità di successo del 90%
        boolean rimborsoSuccesso = RANDOM.nextInt(100) < 90;

        if (rimborsoSuccesso) {
            System.out.println("Rimborso confermato dal gateway.");
        } else {
            throw new InvalidOperationException("Il gateway di pagamento ha rifiutato la richiesta di rimborso.");
        }
    }

    @Transactional(readOnly = true)
    public List<OrdineDTO> getOrdiniByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email).get();
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato con email: " + email);
        }

        List<Ordine> ordini = ordineRepository.findByCliente(cliente);
        List<OrdineDTO> ordiniDTO = new ArrayList<>();

        for (Ordine ordine : ordini) {
            OrdineDTO dto = new OrdineDTO();
            dto.setIdOrdine(ordine.getId());
            dto.setData(ordine.getDataOrdine());
            dto.setStato(ordine.getStato());
            dto.setTotaleOrdine(ordine.getTotale()); // Correzione: impostazione del totale nel DTO

            List<OggettoOrdine> prodottiOrdinati = oggettoOrdineRepository.findByOrdine(ordine);
            List<OggettoOrdineDTO> prodottiDTO = new ArrayList<>();

            for (OggettoOrdine po : prodottiOrdinati) {
                OggettoOrdineDTO oggettoDTO = new OggettoOrdineDTO();
                oggettoDTO.setIdOggetto(po.getId());
                oggettoDTO.setNome(po.getNomeProdotto());
                oggettoDTO.setPrezzo(po.getPrezzo());
                oggettoDTO.setQuantita(po.getQuantita());
                prodottiDTO.add(oggettoDTO);
            }

            dto.setOggetti(prodottiDTO);
            ordiniDTO.add(dto);
        }
        return ordiniDTO;
    }

    public Ordine getOrdineById(Long idOrdine) {
        return ordineRepository.findById(idOrdine).get();
    }
}