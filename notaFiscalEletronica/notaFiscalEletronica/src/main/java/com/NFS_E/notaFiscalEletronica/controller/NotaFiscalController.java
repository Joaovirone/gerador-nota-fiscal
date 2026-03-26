package com.NFS_E.notaFiscalEletronica.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.service.NotaFiscalService;
import com.NFS_E.notaFiscalEletronica.*

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalRequestDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalResponseDTO;
import com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalFiltroDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.PageResponseDTO;



@RestController
@RequestMapping("/api/v1/notas-fiscais")
@RequiredArgsConstructor
public class NotaFiscalController {
    
    private final NotaFiscalService service;
    private final NfeTransmissaoService transmissaoService;
    private final NotaFiscalRepository repository;

    @PostMapping("/criar-nota-fiscal")
    public ResponseEntity<NotaFiscalResponseDTO> criarNota(@RequestBody @Valid NotaFiscalRequestDTO request) {

        NotaFiscalResponseDTO response = service.emitir(request);

        return ResponseEntity.ok(response);
    
    }

    @PostMapping("/{id}/transmitir-nota-fiscal")
    public ResponseEntity<NotaFiscal> transmitirNota(@PathVariable UUID id) {

        NotaFiscal notaBanco = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota Fiscal não encontrada. ID: " + id));

        if (notaBanco.getStatus() == com.NFS_E.notaFiscalEletronica.entity.enums.StatusNota.AUTORIZADA) {
            return ResponseEntity.badRequest().body(notaBanco);
        }

        NotaFiscal notaProcessada = transmissaoService.transmitir(notaBanco);

        repository.save(notaProcessada);

        return ResponseEntity.ok(notaProcessada);
    }

    @PatchMapping("/{id}/cancelar-nota-fiscal")
    public ResponseEntity<NotaFiscalResponseDTO> cancelar(@PathVariable UUID id){

        return ResponseEntity.ok(service.cancelar(id));
    }

    @PatchMapping("/{id}/autorizar-nota-fiscal")
    public ResponseEntity<NotaFiscalResponseDTO> autorizar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.autorizar(id));
    }


    @GetMapping("/listar-nota-fiscal")
    public ResponseEntity<PageResponseDTO<NotaFiscalResponseDTO>> listar(NotaFiscalFiltroDTO filtro,
            @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable paginacao) {
        
        
                return ResponseEntity.ok(service.listarTodas(filtro ,paginacao));
    }
    

}