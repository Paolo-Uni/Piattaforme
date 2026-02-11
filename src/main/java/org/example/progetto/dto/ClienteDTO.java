package org.example.progetto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nome;
    private String cognome; // Aggiunto
    private String email;
    private String telefono;
}