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

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProdottoRepository prodottoRepository;
    
    @Autowired
    private OrdineRepository ordineRepository;
    
    @Autowired
    private TransazioneRepository transazioneRepository;
    
    @Autowired
    private SpedizioneRepository spedizioneRepository;
    
    @Autowired
    private OggettoCarrelloRepository oggettoCarrelloRepository;
    
    @Autowired
    private OggettoOrdineRepository oggettoOrdineRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    private static final Random RANDOM = new Random();

    @Transactional
    public void aggiungiAlCarrello(String email, int idProdotto, int quantita) throws ClienteNotFoundException, ProductNotFoundException, InvalidQuantityException {

        // Recupero l'utente dal database.
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato!");
        }

        // Recupero o creo il carrello dell'utente
        Carrello carrello = carrelloRepository.findByCliente((cliente));
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setCliente(cliente);
            carrello = carrelloRepository.save(carrello);
        } else {
            // Lock del carrello per evitare accessi concorrenti
            entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
        }

        Prodotto prod = prodottoRepository.findAll()  // Recuperi la lista completa dei prodotti
                .stream()  // Crei uno stream dalla lista
                .filter(p -> p.getId() == idProdotto)  // Filtro per idProdotto
                .findFirst()  // Trova il primo prodotto che soddisfa il filtro
                .orElseThrow(() -> new ProductNotFoundException("Prodotto con ID " + idProdotto + " non trovato"));

        // Verifico la disponibilità del prodotto
        int disponibilitaProd = prod.getStock();

        if (quantita > disponibilitaProd) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
        }

        // Controllo se il prodotto è già presente nel carrello

        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello,prod);
        if (oggetto != null) {
            // Lock dell'elemento del carrello
            entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);

            // Aggiorno la quantità
            int nuovaQuantita = oggetto.getQuantita() + quantita;
            if (nuovaQuantita <= prod.getStock()) {
                oggetto.setQuantita(nuovaQuantita);
                oggettoCarrelloRepository.save(oggetto);
            } else {
                throw new InvalidQuantityException("Quantità totale superiore alla disponibilità del prodotto");
            }
        } else {
            // Aggiungo il prodotto al carrello
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
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato!");
        }

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) {
            throw new InvalidCartOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        Prodotto prodotto = prodottoRepository.findById(idProdotto);
        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (oggetto == null) {
            throw new ProductNotFoundException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);


        Prodotto prod = prodottoRepository.findAll()  // Recuperi la lista completa dei prodotti
                .stream()  // Crei uno stream dalla lista
                .filter(p -> Objects.equals(p.getId(), idProdotto))  // Filtro per idProdotto
                .findFirst()  // Trova il primo prodotto che soddisfa il filtro
                .orElseThrow(() -> new ProductNotFoundException("Prodotto con ID " + idProdotto + " non trovato"));

        int disponibilitaProd = prod.getStock();

        if (oggetto.getQuantita() + 1 > disponibilitaProd) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: " +
                    "il prodotto non è disponibile per la quantità desiderata");
        }
        oggetto.setQuantita(oggetto.getQuantita() + 1);
        oggettoCarrelloRepository.save(oggetto);
    }

    @Transactional
    public void rimuoviDalCarrello(String email, Long prodottoID)
            throws ClienteNotFoundException, InvalidCartOperationException {

        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato!");
        }

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) {
            throw new InvalidCartOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        Prodotto prodotto = prodottoRepository.findById(prodottoID);
        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (oggetto == null) {
            throw new InvalidCartOperationException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);

        // Rimuovo il prodotto dal carrello
        oggettoCarrelloRepository.delete(oggetto);
    }

    @Transactional
    public void decrementaQuantitaOggettoCarrello(String email, Long idProdotto)
            throws ClienteNotFoundException, InvalidCartOperationException {

        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato!");
        }

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) {
            throw new InvalidCartOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        Prodotto prodotto = prodottoRepository.findById(idProdotto);
        OggettoCarrello oggetto = oggettoCarrelloRepository.findByCarrelloAndProdotto(carrello, prodotto);
        if (oggetto == null) {
            throw new InvalidCartOperationException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);

        if (!oggetto.getCarrello().equals(carrello)) {
            throw new InvalidCartOperationException("Operazione non valida: il carrello non corrisponde");
        }

        // Decremento o rimuovo il prodotto dal carrello
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
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato!");
        }

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) {
            throw new InvalidCartOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko tutti gli elementi del carrello
        Set<OggettoCarrello> oggetti = oggettoCarrelloRepository.findByCarrello(carrello);
        for (OggettoCarrello oggetto : oggetti) {
            entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);
        }

        // Svuoto il carrello
        oggettoCarrelloRepository.deleteAllByCarrello(carrello);
    }

    @Transactional
    public void ordina(String email, String indirizzoSpedizione)
            throws ClienteNotFoundException, InvalidCartOperationException, InvalidQuantityException{

        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato!");
        }

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) {
            throw new InvalidCartOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko gli elementi del carrello
        Set<OggettoCarrello> prodottiCliente = oggettoCarrelloRepository.findByCarrello(carrello);
        if (prodottiCliente.isEmpty()) {
            throw new InvalidCartOperationException("Il carrello è vuoto. Aggiungi prodotti prima di procedere all'ordine.");
        }

        // Ordino gli elementi per evitare deadlock
        List<OggettoCarrello> oggetti = new ArrayList<>(prodottiCliente);
        oggetti.sort(Comparator.comparingLong(OggettoCarrello::getId));

        // Verifico la disponibilità e locko i prodotti
        for (OggettoCarrello oggetto : oggetti) {
            // Lock dell'elemento del carrello
            entityManager.lock(oggetto, LockModeType.PESSIMISTIC_WRITE);

            Prodotto prodotto = prodottoRepository.findById(oggetto.getProdotto().getId());
            // Lock del prodotto
            entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);

            // Verifico la disponibilità
            if (prodotto.getStock() < oggetto.getQuantita()) {
                throw new InvalidQuantityException("La quantità del prodotto '" + prodotto.getNome() + "' non è sufficiente per completare l'ordine.");
            }

            // Decremento la disponibilità del prodotto
            prodotto.setStock(prodotto.getStock() - oggetto.getQuantita());
            prodottoRepository.save(prodotto);
        }

        // Creazione dell'ordine e della transazione
        Ordine ordine = new Ordine();
        Transazione transazione = new Transazione();

        ordine.setCliente(cliente);
        ordine.setDataOrdine(LocalDateTime.now());
        ordine.setStato("Processamento in corso...");
        ordineRepository.save(ordine);

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
            svuotaCarrello(email);

            // Salvo i prodotti ordinati
            for (OggettoCarrello oc : oggetti) {
                OggettoOrdine oggetto = new OggettoOrdine();
                oggetto.setNomeProdotto(oc.getProdotto().getNome());
                oggetto.setTaglia(oc.getProdotto().getTaglia());
                oggetto.setColore(oc.getProdotto().getColore());
                oggetto.setDescrizione(oc.getProdotto().getDescrizione());
                oggetto.setPrezzo(oc.getProdotto().getPrezzo());
                oggetto.setQuantita(oc.getQuantita());
                oggetto.setOrdine(ordine);
                ordine.getOggetti().add(oggetto);
                oggettoOrdineRepository.save(oggetto);
            }
            ordine.setTotale(importo);

        } else {
            transazione.setEsito(false);
            ordine.setStato("Pagamento fallito");

            // Ripristino la quantità dei prodotti
            for (OggettoCarrello oc : oggetti) {
                Prodotto prodotto = prodottoRepository.findById(oc.getProdotto().getId());
                // Lock del prodotto
                entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);

                prodotto.setStock(prodotto.getStock() + oc.getQuantita());
                prodottoRepository.save(prodotto);
            }

            throw new PaymentException("Il pagamento è fallito. Riprovare.");
        }

        // Salvo le transazioni e la spedizione
        transazioneRepository.save(transazione);
        spedizioneRepository.save(spedizione);
        ordineRepository.save(ordine);
    }

    private static BigDecimal calcolaImporto(Set<OggettoCarrello> prodottiUser) {
        BigDecimal totale = BigDecimal.ZERO;

        if (prodottiUser == null || prodottiUser.isEmpty()) {
            return totale;
        }

        for (OggettoCarrello oggetto : prodottiUser) {
            Prodotto prodotto = oggetto.getProdotto();
            BigDecimal prezzo = prodotto.getPrezzo();
            int quantita = oggetto.getQuantita();

            if (prezzo == null || prezzo.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Prezzo del prodotto " + prodotto.getNome() + " non valido.");
            }
            if (quantita <= 0) {
                throw new IllegalArgumentException("Quantità del prodotto " + prodotto.getNome() + " non valida.");
            }

            BigDecimal costoProdotto = prezzo.multiply(BigDecimal.valueOf(quantita));
            totale = totale.add(costoProdotto);
        }

        return totale;
    }

    private boolean processaPagamento(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Errore: importo non valido. Valore importo: " + amount);
            return false;
        }

        System.out.println("Importo da pagare: " + amount);

        boolean pagamentoRiuscito = RANDOM.nextInt(100) < 80;

        if (pagamentoRiuscito) {
            System.out.println("Pagamento effettuato con successo!");
            return true;
        } else {
            System.out.println("Pagamento fallito.");
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<OggettoCarrelloDTO> getCartItemsByEmail(String email) throws ClienteNotFoundException {

        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente non trovato!");
        }

        Carrello carrello = carrelloRepository.findByCliente(cliente);
        if (carrello == null) {
            throw new InvalidCartOperationException("Il carrello dell'utente non è stato trovato.");
        }

        Set<OggettoCarrello> prodottiUser = oggettoCarrelloRepository.findByCarrello(carrello);

        List<OggettoCarrelloDTO> cartItems = new ArrayList<>();

        for (OggettoCarrello oggetto : prodottiUser) {
            Prodotto prodotto = oggetto.getProdotto();

            OggettoCarrelloDTO dto = new OggettoCarrelloDTO();
            dto.setIdProdotto(prodotto.getId());
            dto.setQuantita(oggetto.getQuantita());

            cartItems.add(dto);
        }

        return cartItems;
    }
}
