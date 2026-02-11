package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordine")
@Getter @Setter @ToString @EqualsAndHashCode
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

    @OneToOne(mappedBy = "ordine")
    private Spedizione spedizione;

    @Column(name="stato", nullable = false)
    private String stato;

    @OneToOne(mappedBy = "ordine")
    private Transazione transazione;

    @OneToMany(mappedBy = "ordine",cascade = CascadeType.ALL)
    private List<OggettoOrdine> oggetti = new ArrayList<>();

}
