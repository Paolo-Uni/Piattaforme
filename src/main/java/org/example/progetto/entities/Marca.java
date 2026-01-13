package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "marca")
@Getter @Setter @EqualsAndHashCode @ToString
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "marca_id", nullable = false)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

}
