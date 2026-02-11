package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrello")
@Getter @Setter
public class Carrello {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "carrello_id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cliente")
    private Cliente cliente;

    @Column(name = "totale_carrello", precision = 10, scale = 2)
    private BigDecimal totaleCarrello = BigDecimal.ZERO;

    @OneToMany(mappedBy = "carrello", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OggettoCarrello> oggetti = new ArrayList<>();
}