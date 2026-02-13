package org.example.progetto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdineDTO {
    private Long idOrdine;
    private LocalDateTime data;
    private String stato;
    private BigDecimal totaleOrdine;
    private List<OggettoOrdineDTO> oggetti;
}