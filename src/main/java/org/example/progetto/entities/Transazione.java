package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transazione")
@Getter @Setter @ToString @EqualsAndHashCode
public class Transazione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transazione_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordine", insertable = false, updatable = false)
    private Ordine ordine;

    @Column(name = "data", nullable = false)
    private Instant data;

    @Column(name = "importo", nullable = false)
    private BigDecimal importo;

    @JoinColumn(name = "metodo_di_pagamento", nullable = false)
    private String metodoDiPagamento;

    @Column(name = "esito", nullable = false)
    private boolean esito;
}
