package org.example.progetto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteUpdateRequest {
    private String nome;
    private String cognome;
}
