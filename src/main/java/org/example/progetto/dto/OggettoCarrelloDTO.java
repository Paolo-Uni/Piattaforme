package org.example.progetto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OggettoCarrelloDTO {
    private Long idProdotto;
    private String nomeProdotto;
    private BigDecimal prezzoUnitario;
    private String colore;
    private String taglia;
    private Integer quantita;
}