package com.NFS_E.notaFiscalEletronica.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "itens_notas_fiscais")
@Data
public class ItemNotaFiscal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID )
    private UUID id;

    @Column(name= "descricao", nullable=false)
    private String descricao;

    @Column(name= "quantidade", nullable=false)
    private BigDecimal quantidade;;

    @Column(name= "valor_unitario", nullable=false)
    private BigDecimal valorUnitario;

    @Column(name= "valor_total", nullable=false)
    private BigDecimal valorTotal;

    @Column(name= "ncm", nullable = false)
    private String ncm;
    
    @Column(name= "cfop", nullable = false)
    private String cfop;
    
    @Column(name = "cst_icms", nullable = false)
    private String cstIcms;


    //campos do imposto 
    @Column(name= "base_calculo_icms", nullable = false)
    private BigDecimal baseCalculoIcms;

    @Column(name= "aliquota_icms", nullable = false)
    private BigDecimal aliquotaIcms;

    @Column(name= "valor_icms", nullable = false)
    private BigDecimal valorIcms;


    @ManyToOne
    @JoinColumn(name="nota_id")
    private NotaFiscal notaFiscal;


    public ItemNotaFiscal(){}


    public void calcularSubtotal(){

        if (this.quantidade != null && this.valorUnitario != null){
            this.valorTotal = this.quantidade.multiply(this.valorUnitario);
        }
    }

    public void calcularImpostos() {
        this.baseCalculoIcms = this.valorTotal != null ? this.valorTotal : BigDecimal.ZERO;
        if (this.aliquotaIcms == null) {
            this.aliquotaIcms = BigDecimal.ZERO;
        }
        this.valorIcms = this.baseCalculoIcms.multiply(this.aliquotaIcms)
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}

