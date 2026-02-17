package org.example.progetto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "spedizione")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Spedizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spedizione_id", nullable = false)
    private Long id;

    // Aumentato a 255 caratteri, 50 sono pochi per un indirizzo
    @Column(name = "indirizzo_spedizione", nullable = false)
    private String indirizzoSpedizione;

    @Column(name = "data_prevista", nullable = false)
    private Instant dataPrevista;

    @Column(name = "stato", nullable = false, length = 50)
    private String stato;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordine")
    @JsonIgnore // Evita ricorsione
    private Ordine ordine;
}