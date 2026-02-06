package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrello")
@Getter @Setter @EqualsAndHashCode @ToString
public class Carrello {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "carrello_id", nullable = false)
    private int id;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "totale_carrello", precision = 10, scale = 2)
    private BigDecimal totaleCarrello = BigDecimal.ZERO;

    @OneToMany(mappedBy = "carrello", cascade = CascadeType.ALL)
    private List<OggettoCarrello> oggetti = new ArrayList<>();

}
