package org.example.progetto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class Request {
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
}