package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prodotto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nome", "categoria_id", "marca_marca_id"})
})
@Getter @Setter @ToString @EqualsAndHashCode
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "prodotto_id", nullable = false)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descrizione")
    private String descrizione;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "marca_marca_id", nullable = false)
    private Marca marca;

    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL)
    private List<VarianteProdotto> varianti = new ArrayList<>();

}
