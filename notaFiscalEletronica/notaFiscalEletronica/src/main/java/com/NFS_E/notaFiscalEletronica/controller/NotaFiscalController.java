package com.NFS_E.notaFiscalEletronica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalRequest;
import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.service.NotaFiscalService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal;


@RestController
@RequestMapping("/api/v1/notas-fiscais")
@RequiredArgsConstructor
public class NotaFiscalController {
    
    private final NotaFiscalService service;


    @PostMapping
    public ResponseEntity<NotaFiscal> criarNota(@RequestBody NotaFiscalRequest request) {
        
        NotaFiscal nota = new NotaFiscal();

        nota.setNotaFiscalDestino(request.notaFiscalDestino());

        var itens = request.itens().stream().map(itemDto -> {

            ItemNotaFiscal item = new ItemNotaFiscal();
            item.setDescricao(itemDto.descricao());
            item.setQuantidade(itemDto.quantidade());
            item.setValorUnitario(itemDto.valorUnitario());
            item.calcularSubtotal();

            return item;
        }).collect(Collectors.toList());
        
        nota.setItens(itens);

        NotaFiscal notaSalva = service.emitir(nota);

        return ResponseEntity.ok(notaSalva);
    }
    
}
