package org.example.progetto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteUpdateRequest {
    // Rendiamo i campi opzionali: l'utente manda solo quello che vuole cambiare
    private String nome;
    private String cognome;
    private String telefono;
    private String indirizzo;
}