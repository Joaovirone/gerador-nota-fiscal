package com.NFS_E.notaFiscalEletronica.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemNotaFiscalResponseDTO(

    UUID id,
    String descricao,
    BigDecimal quantidade,
    BigDecimal valorUnitario,
    BigDecimal valorTotal
) {
    

}
