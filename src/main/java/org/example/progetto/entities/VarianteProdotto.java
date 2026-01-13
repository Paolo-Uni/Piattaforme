package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "variante", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"prodotto_id", "taglia", "colore"})
})
@Getter @Setter @EqualsAndHashCode @ToString
public class VarianteProdotto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "variante_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    @Column(name = "taglia", nullable = false)
    private String taglia;

    @Column(name = "colore", nullable = false)
    private String colore;

    @Column(name = "prezzo", nullable = false)
    private double prezzo;

    @Column(name = "stock", nullable = false)
    private int stock;

}
