package com.NFS_E.notaFiscalEletronica.controller.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;

@Data
public class PageResponseDTO<T> {

    private List<T> conteudo;
    private int paginaAtual;
    private long totalElementos;
    private int totalPaginas;
    private int tamanhoPagina;
    private boolean ultima;

   
    public PageResponseDTO(Page<T> page) {
        this.conteudo = page.getContent();
        this.paginaAtual = page.getNumber();
        this.totalElementos = page.getTotalElements();
        this.totalPaginas = page.getTotalPages();
        this.tamanhoPagina = page.getSize();
        this.ultima = page.isLast();
    }
}

