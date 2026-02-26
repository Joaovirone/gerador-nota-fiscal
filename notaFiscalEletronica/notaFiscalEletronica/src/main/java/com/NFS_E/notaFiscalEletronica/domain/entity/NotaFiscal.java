package com.NFS_E.notaFiscalEletronica.domain.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.NFS_E.notaFiscalEletronica.domain.entity.enums.StatusNota;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotaFiscal {
    
    private final UUID id;
    private final String docDestino;
    private BigDecimal valorTotal;
    private StatusNota status;
    private List<ItemNotaFiscal> itens;

    public NotaFiscal(String docDestino){
        this.id = UUID.randomUUID();
        this.docDestino = docDestino;
        this.status = StatusNota.DIGITACAO;
        this.valorTotal = BigDecimal.ZERO;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ItemNotaFiscal item){

        this.itens.add(item);
        this.valorTotal = this.valorTotal.add(item.getValorTotal());
    }

}
