package org.example.progetto.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class OrdineDTO {
    private Long idOrdine;
    private LocalDateTime data;
    private String stato;
    private BigDecimal totaleOrdine;
    private List<OggettoOrdineDTO> oggetti;
}
