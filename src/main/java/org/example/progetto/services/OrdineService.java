package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.progetto.dto.OggettoOrdineDTO;
import org.example.progetto.dto.OrdineDTO;
import org.example.progetto.entities.*;
import org.example.progetto.exceptions.ClienteNotFoundException;
import org.example.progetto.exceptions.InvalidOperationException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.repositories.ClienteRepository;
import org.example.progetto.repositories.OggettoOrdineRepository;
import org.example.progetto.repositories.OrdineRepository;
import org.example.progetto.repositories.ProdottoRepository;
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
    private ProdottoRepository prodottoRepository;

    @Autowired
    private OggettoOrdineRepository oggettoOrdineRepository;

    @Transactional
    public void annullaOrdine(Cliente c, Ordine o, String motivo) {
        // Lock sull'utente
        if(!clienteRepository.existsById(c.getId()))
            throw new ClienteNotFoundException("Cliente non trovato");
        entityManager.lock(c, LockModeType.PESSIMISTIC_WRITE);

        // Lock sull'ordine
        if(!ordineRepository.existsById(o.getId()))
            throw new InvalidOperationException("Ordine non trovato");
        entityManager.lock(o, LockModeType.PESSIMISTIC_WRITE);

        if (!((o.getCliente().getId()).equals(c.getId()))) {
            throw new InvalidOperationException("L'ordine non appartiene all'utente.");
        }

        // Lock sulla spedizione
        Spedizione sped = o.getSpedizione();
        if (sped != null) {
            entityManager.lock(sped, LockModeType.PESSIMISTIC_WRITE);
            sped.setStato("Ordine annullato!");
            // Se necessario, salvo la spedizione
            // spedizioneRepository.save(sped);
        }

        o.setStato("Annullato. Motivo: " + motivo);
        ordineRepository.save(o);

        // Processo il rimborso
        processaRimborso(o);
    }

    private void processaRimborso(Ordine ordine) {
        BigDecimal importoRimborso = calcolaImportoRimborso(ordine);
        simulaRimborso(importoRimborso);
    }

    private BigDecimal calcolaImportoRimborso(Ordine ordine) {
        // Lock sulla transazione
        Transazione transazione = ordine.getTransazione();
        if (transazione != null) {
            entityManager.lock(transazione, LockModeType.PESSIMISTIC_WRITE);
            return transazione.getImporto();
        } else {
            throw new InvalidOperationException("Transazione non trovata per l'ordine.");
        }
    }

    private void simulaRimborso(BigDecimal importo) {
        // Logica di esempio per simulare un rimborso
        System.out.println("Rimborso in corso. Importo: " + importo);

        // Simulazione di una probabilità di successo del 90%
        boolean rimborsoSuccesso = RANDOM.nextInt(100) < 90;

        if (rimborsoSuccesso) {
            System.out.println("Rimborso completato con successo!");
        } else {
            System.out.println("Rimborso fallito.");
            throw new InvalidOperationException("Il rimborso è fallito.");
        }
    }

    @Transactional(readOnly = true)
    public List<OrdineDTO> getOrdiniByEmail(String email) throws ClienteNotFoundException, ProductNotFoundException {
        // Recupero il cliente tramite la sua email
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato con email: " + email);
        }

        // Log per verificare il cliente trovato
        System.out.println("Cliente trovato: " + cliente.getEmail());

        // Recupero gli ordini del cliente
        List<Ordine> ordini = ordineRepository.findByCliente(cliente);
        if (ordini.isEmpty()) {
            System.out.println("Nessun ordine trovato per il cliente con email: " + email);
        }

        // Creazione della lista di DTO per gli ordini
        List<OrdineDTO> ordiniDTO = new ArrayList<>();
        for (Ordine ordine : ordini) {
            OrdineDTO dto = new OrdineDTO();
            dto.setIdOrdine(ordine.getId());
            dto.setData(ordine.getDataOrdine());
            dto.setStato(ordine.getStato());


            // Log per ogni ordine trovato
            System.out.println("Ordine trovato: ID = " + ordine.getId() + ", Stato = " + ordine.getStato());

            // Recupero dei prodotti ordinati per ciascun ordine
            List<OggettoOrdine> prodottiOrdinati = oggettoOrdineRepository.findOggettiByOrdine(ordine);
            List<OggettoOrdineDTO> prodottiDTO = new ArrayList<>();

            for (OggettoOrdine po : prodottiOrdinati) {
                
                OggettoOrdineDTO oggettoDTO = new OggettoOrdineDTO();
                oggettoDTO.setIdOggetto(po.getId());
                oggettoDTO.setNome(po.getNomeProdotto());
                oggettoDTO.setPrezzo(po.getPrezzo());
                oggettoDTO.setQuantita(po.getQuantita());

                // Aggiungi il prodotto al DTO dell'ordine
                prodottiDTO.add(oggettoDTO);
            }

            // Aggiungi la lista di prodotti all'ordineDTO
            dto.setOggetti(prodottiDTO);

            // Aggiungi l'ordineDTO alla lista finale
            ordiniDTO.add(dto);
        }

        return ordiniDTO;
    }

}
