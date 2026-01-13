package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "oggetto_carrello", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"variante", "carrello_carrello_id"})
})
@Getter @Setter @ToString @EqualsAndHashCode
public class OggettoCarrello {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "oggetto_carrello_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProdotto variante;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @ManyToOne
    @JoinColumn(name = "carrello_carrello_id", nullable = false)
    private Carrello carrello;

}
