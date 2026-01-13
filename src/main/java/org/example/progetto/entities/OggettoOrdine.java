package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "oggetto_ordine")
@Getter @Setter @ToString @EqualsAndHashCode
public class OggettoOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_ordine", nullable = false)
    private Long id;

    @Column(name = "nome_prodotto", nullable = false)
    private String nomeProdotto;

    @Column(name = "taglia", nullable = false)
    private String taglia;

    @Column(name = "colore", nullable = false)
    private String colore;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "prezzo", nullable = false)
    private double prezzo;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @ManyToOne
    @JoinColumn(name = "ordine_id")
    private Ordine ordine;

}
