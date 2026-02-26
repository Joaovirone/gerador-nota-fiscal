package com.NFS_E.notaFiscalEletronica.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name= "valor_unidade", nullable=false)
    private BigDecimal valorUnitario;

    @Column(name= "valor_total", nullable=false)
    private BigDecimal valorTotal;


    public ItemNotaFiscal(){}


    public void calcularSubtotal(){

        if (this.quantidade != null && this.valorUnitario != null){
            this.valorTotal = this.quantidade.multiply(this.valorUnitario);
        }
    }
}
