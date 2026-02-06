package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "spedizione")
@Getter @Setter @ToString @EqualsAndHashCode
public class Spedizione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "spedizione_id", nullable = false)
    private Long id;

    @Column(name = "indirizzo_spedizione", nullable = false, length = 50)
    private String indirizzoSpedizione;

    @Column(name = "data_prevista", nullable = false)
    private Instant dataPrevista;

    @Column(name = "stato", nullable = false, length = 50)
    private String stato;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordine", insertable = false, updatable = false)
    private Ordine ordine;
}
