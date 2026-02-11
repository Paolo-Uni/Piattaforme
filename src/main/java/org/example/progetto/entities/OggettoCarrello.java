package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "oggetto_carrello", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"prodotto", "carrello"})
})
@Getter @Setter @ToString @EqualsAndHashCode
public class OggettoCarrello {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "oggetto_carrello_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prodotto", nullable = false)
    private Prodotto prodotto;

    @Column(name = "quantita", nullable = false)
    private Integer quantita;

    @ManyToOne
    @JoinColumn(name = "carrello", nullable = false)
    private Carrello carrello;

}
