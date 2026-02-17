package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.example.progetto.dto.OggettoOrdineDTO;
import org.example.progetto.dto.OrdineDTO;
import org.example.progetto.entities.*;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.InvalidOperationException;
import org.example.progetto.repositories.ClienteRepository;
import org.example.progetto.repositories.OrdineRepository;
import org.example.progetto.repositories.ProdottoRepository;
import org.example.progetto.support.StatoOrdine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrdineService {

    private static final Random RANDOM = new Random();

    private final EntityManager entityManager;
    private final OrdineRepository ordineRepository;
    private final ClienteRepository clienteRepository;
    private final ProdottoRepository prodottoRepository; // Necessario per ripristinare lo stock

    @Transactional
    public void annullaOrdine(Long idOrdine, String emailCliente, String motivo) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente non trovato"));
        
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new InvalidOperationException("Ordine non trovato"));
        
        entityManager.lock(ordine, LockModeType.PESSIMISTIC_WRITE);

        if (!ordine.getCliente().getId().equals(cliente.getId())) {
            throw new InvalidOperationException("L'ordine non appartiene all'utente specificato.");
        }

        StatoOrdine statoAttuale = ordine.getStato();
        if (statoAttuale == StatoOrdine.ANNULLATO || statoAttuale == StatoOrdine.SPEDITO || statoAttuale == StatoOrdine.CONSEGNATO) {
            throw new InvalidOperationException("Impossibile annullare l'ordine nello stato attuale: " + statoAttuale);
        }

        // 1. Gestione Spedizione
        Spedizione sped = ordine.getSpedizione();
        if (sped != null) {
            entityManager.lock(sped, LockModeType.PESSIMISTIC_WRITE);
            sped.setStato("Annullata a seguito di cancellazione ordine");
        }

        // 2. Ripristino dello Stock (CORREZIONE IMPORTANTE)
        for (OggettoOrdine item : ordine.getOggetti()) {
            // Cerchiamo il prodotto originale. 
            // Se abbiamo salvato l'ID originale in OggettoOrdine usiamo quello, altrimenti cerchiamo per nome o ID se disponibile.
            // Qui assumiamo che OggettoOrdine abbia il riferimento o l'ID (come aggiunto nella fix delle Entity).
            if (item.getProdottoId() != null) {
                prodottoRepository.findById(item.getProdottoId()).ifPresent(prodotto -> {
                    entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);
                    prodotto.setStock(prodotto.getStock() + item.getQuantita());
                    prodottoRepository.save(prodotto);
                });
            }
        }

        ordine.setStato(StatoOrdine.ANNULLATO);
        ordine.setNote("Motivo annullamento: " + motivo);
        ordineRepository.save(ordine);

        try {
            processaRimborso(ordine);
        } catch (Exception e) {
            System.err.println("Fallimento rimborso per ordine " + ordine.getId() + ": " + e.getMessage());
            ordine.setNote(ordine.getNote() + " | ERRORE: Rimborso FALLITO, contattare assistenza.");
            ordineRepository.save(ordine);
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
            return BigDecimal.ZERO; 
        }
    }

    private void simulaRimborso(BigDecimal importo) {
        if (importo.compareTo(BigDecimal.ZERO) == 0) return;
        
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
            ordiniDTO.add(mapToDTO(ordine));
        }
        return ordiniDTO;
    }

    @Transactional(readOnly = true)
    public OrdineDTO getOrdineDTOById(Long idOrdine) {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new InvalidOperationException("Ordine non trovato con ID: " + idOrdine));
        return mapToDTO(ordine);
    }

    // Metodo helper per la mappatura Entity -> DTO
    private OrdineDTO mapToDTO(Ordine ordine) {
        OrdineDTO dto = new OrdineDTO();
        dto.setIdOrdine(ordine.getId());
        dto.setData(ordine.getDataOrdine());
        dto.setStato(ordine.getStato().toString());
        dto.setTotaleOrdine(ordine.getTotale());
        
        // FIX: Mappatura corretta dell'indirizzo spedizione
        if (ordine.getSpedizione() != null) {
            dto.setIndirizzoSpedizione(ordine.getSpedizione().getIndirizzoSpedizione());
        }

        List<OggettoOrdineDTO> prodottiDTO = new ArrayList<>();
        if (ordine.getOggetti() != null) {
            for (OggettoOrdine po : ordine.getOggetti()) {
                OggettoOrdineDTO oggettoDTO = new OggettoOrdineDTO();
                oggettoDTO.setIdOggetto(po.getId());
                oggettoDTO.setNome(po.getNomeProdotto());
                oggettoDTO.setTaglia(po.getTaglia());
                oggettoDTO.setColore(po.getColore());
                oggettoDTO.setPrezzo(po.getPrezzo());
                oggettoDTO.setQuantita(po.getQuantita());
                prodottiDTO.add(oggettoDTO);
            }
        }
        dto.setOggetti(prodottiDTO);
        return dto;
    }
}