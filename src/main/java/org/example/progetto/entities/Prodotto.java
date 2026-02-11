package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "prodotto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nome", "categoria", "marca", "colore", "taglia"})
})
@Getter @Setter
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "prodotto_id", nullable = false)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "colore")
    private String colore;

    @Column(name = "taglia")
    private String taglia;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "prezzo", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "marca", nullable = false)
    private String marca;
}