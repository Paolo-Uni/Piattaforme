package org.example.progetto.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter 
@Setter
public class OggettoOrdineDTO {
    private Long idOggetto;
    private String nome;
    private String taglia; // Aggiunto
    private String colore; // Aggiunto
    private BigDecimal prezzo;
    private Integer quantita;
}