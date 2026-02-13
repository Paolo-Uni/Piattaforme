package org.example.progetto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transazione")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transazione_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordine")
    @JsonIgnore // Evita ricorsione
    private Ordine ordine;

    @Column(name = "data", nullable = false)
    private Instant data;

    @Column(name = "importo", nullable = false)
    private BigDecimal importo;

    @Column(name = "esito", nullable = false)
    private boolean esito;
}