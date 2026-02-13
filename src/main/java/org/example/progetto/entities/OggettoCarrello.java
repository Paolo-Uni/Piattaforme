package org.example.progetto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "oggetto_carrello", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"prodotto", "carrello"})
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OggettoCarrello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oggetto_carrello_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prodotto", nullable = false)
    private Prodotto prodotto;

    @Column(name = "quantita", nullable = false)
    private Integer quantita;

    @ManyToOne
    @JoinColumn(name = "carrello", nullable = false)
    @JsonIgnore // Fondamentale per evitare loop infiniti
    private Carrello carrello;
}