package org.example.progetto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OggettoOrdineDTO {
    private Long idOggetto;
    private String nome;
    private String taglia;
    private String colore;
    private BigDecimal prezzo;
    private Integer quantita;
}