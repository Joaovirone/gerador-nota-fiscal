package com.NFS_E.notaFiscalEletronica.controller.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemNotaFiscalResponseDTO{
    
    @NotBlank(message = "A descrição do item é obrigatória")
    private String descricao;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    @NotNull(message = "O valor unitário é obrigatório")
    @PositiveOrZero(message = "O valor unitário não pode ser negativo")
    private BigDecimal valorUnitario;

    @NotBlank(message = "O NCM é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "O NCM deve conter exatamente 8 dígitos numéricos")
    private String ncm;

    @NotBlank(message = "O CFOP é obrigatório")
    @Pattern(regexp = "\\d{4}", message = "O CFOP deve conter exatamente 4 dígitos numéricos")
    private String cfop;

    @NotBlank(message = "O CST de ICMS é obrigatório")
    @Size(min = 2, max = 3, message = "O CST deve ter 2 ou 3 caracteres")
    private String cstIcms;

    @NotNull(message = "A alíquota de ICMS é obrigatória")
    @DecimalMin(value = "0.0", message = "A alíquota não pode ser negativa")
    @DecimalMax(value = "100.0", message = "A alíquota não pode ser maior que 100%")
    private BigDecimal aliquotaIcms;

}
