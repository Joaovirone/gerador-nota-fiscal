package com.NFS_E.notaFiscalEletronica.controller.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.NFS_E.notaFiscalEletronica.controller.dto.ItemNotaFiscalResponseDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalRequestDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalResponseDTO;
import com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal;
import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

@Component
public class NotaFiscalMapper {
    
    public NotaFiscal toEntity(NotaFiscalRequestDTO dto){

        NotaFiscal nota =new NotaFiscal();

        nota.setNotaFiscalDestino(dto.notaFsicalDestino());

        List<ItemNotaFiscal> itens = dto.itens().stream()
        .map(itemDto -> {
            ItemNotaFiscal item = new ItemNotaFiscal();
            item.setDescricao(itemDto.descricao());
            item.setQuantidade(itemDto.quantidade());
            item.setValorUnitario(itemDto.valorUnitario());

            return item;
        }).collect(Collectors.toList());

        nota.setItens(itens);
        return nota;
    }

    public NotaFiscalResponseDTO toResponse(NotaFiscal entity){

        List<ItemNotaFiscalResponseDTO> itensDto = entity.getItens().stream()
        .map(item -> new ItemNotaFiscalResponseDTO(
            item.getId(), 
            item.getDescricao(),
            item.getQuantidade(), 
            item.getValorUnitario(), 
            item.getValorTotal()
        )).toList();

        return new NotaFiscalResponseDTO(
            entity.getId(),
            entity.getNotaFiscalDestino(),
            entity.getValorTotal(),
            entity.getStatus().name(),
            itensDto
        );

    }
}   
