package org.example.progetto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClienteDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefono;

    public ClienteDTO() {}

    public ClienteDTO(Long id, String nome, String email, String telefono) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefono = telefono;
    }

}