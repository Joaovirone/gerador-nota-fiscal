package com.NFS_E.notaFiscalEletronica.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.NFS_E.notaFiscalEletronica.entity.enums.StatusNota;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="notas_fiscais")
@Data
@EntityListeners(AuditingEntityListener.class)
public class NotaFiscal {
    
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name= "id", nullable=false)
    private UUID id;

    @Column(unique = true)
    private Long numero;

    private Integer serie = 1;

    @Column(length= 44, unique = true)
    private String chaveAcesso;

    @Column(name="nota_fiscal_destino", nullable=false)
    private String notaFiscalDestino;

    @Column(name="valor_total_nf", nullable=false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private StatusNota status = StatusNota.DIGITACAO;

    @OneToMany(mappedBy="notaFiscal", cascade= CascadeType.ALL, orphanRemoval= true)
    private List<ItemNotaFiscal> itens = new ArrayList<>();

    @Column(length = 8)
    private String codigoNumerico;


    @CreatedDate
    @Column(nullable=false, updatable=false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataUltimaAtualização;


    public void calcularTotal(){

        this.valorTotal = itens.stream()
                .map(item -> {
                    item.calcularSubtotal();
                    item.calcularImpostos();
                    return item.getValorTotal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal :: add);
    }

    public String getCodigoNumerico() {
        if (this.codigoNumerico == null) {
            gerarCodigoNumerico();
        }
        return this.codigoNumerico;
    }

    public void gerarCodigoNumerico() {
    if (this.codigoNumerico == null) {
        // Gera 8 dígitos aleatórios
        this.codigoNumerico = String.format("%08d", new java.util.Random().nextInt(100000000));
    }
}
}
