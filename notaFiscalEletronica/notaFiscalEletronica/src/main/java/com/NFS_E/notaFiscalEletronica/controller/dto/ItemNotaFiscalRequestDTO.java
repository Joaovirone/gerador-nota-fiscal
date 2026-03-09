package com.NFS_E.notaFiscalEletronica.controller.dto;

import java.math.BigDecimal;

public record ItemNotaFiscalRequestDTO(
    
        String descricao,
        BigDecimal quantidade,
        BigDecimal valorUnitario
) {
   
}
