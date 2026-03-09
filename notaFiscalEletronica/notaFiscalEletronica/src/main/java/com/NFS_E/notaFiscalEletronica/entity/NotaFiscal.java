package com.NFS_E.notaFiscalEletronica.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.NFS_E.notaFiscalEletronica.entity.enums.StatusNota;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="notas_fiscais")
@Data
public class NotaFiscal {
    
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name= "id", nullable=false)
    private UUID id;

    @Column(name="nota_fiscal_destino", nullable=false)
    private String notaFiscalDestino;

    @Column(name="valor_total_nf", nullable=false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private StatusNota status = StatusNota.DIGITACAO;

    @OneToMany(mappedBy="notaFiscal", cascade= CascadeType.ALL, orphanRemoval= true)
    @JoinColumn(name= "nota_id")
    private List<ItemNotaFiscal> itens = new ArrayList<>();


    public void calcularTotal(){

        this.valorTotal = itens.stream()
                .map(item -> {
                    item.calcularSubtotal();
                    return item.getValorTotal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal :: add);
    }
}
