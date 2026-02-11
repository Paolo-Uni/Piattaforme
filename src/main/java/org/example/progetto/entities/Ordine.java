package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.progetto.support.StatoOrdine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordine")
@Getter @Setter
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ordine_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente")
    private Cliente cliente;

    @Column(name = "data_ordine", nullable = false)
    private LocalDateTime dataOrdine;

    @Column(name = "totale", nullable = false)
    private BigDecimal totale = BigDecimal.ZERO;

    @OneToOne(mappedBy = "ordine", cascade = CascadeType.ALL)
    private Spedizione spedizione;

    @Enumerated(EnumType.STRING)
    @Column(name="stato", nullable = false)
    private StatoOrdine stato;

    // Aggiunto per salvare motivazioni annullamento o errori rimborso
    @Column(name = "note")
    private String note;

    @OneToOne(mappedBy = "ordine", cascade = CascadeType.ALL)
    private Transazione transazione;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL)
    private List<OggettoOrdine> oggetti = new ArrayList<>();
}