package org.example.progetto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "oggetto_ordine")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OggettoOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oggetto_ordine_id", nullable = false)
    private Long id;

    @Column(name = "nome_prodotto", nullable = false)
    private String nomeProdotto;

    @Column(name = "taglia", nullable = false)
    private String taglia;

    @Column(name = "colore", nullable = false)
    private String colore;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "prezzo", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo;

    @Column(name = "quantita", nullable = false)
    private Integer quantita;

    @ManyToOne
    @JoinColumn(name = "ordine")
    @JsonIgnore // Evita ricorsione
    private Ordine ordine;

}