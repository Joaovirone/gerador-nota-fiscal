package com.NFS_E.notaFiscalEletronica.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.infra.sefaz.service.DanfeService;
import com.NFS_E.notaFiscalEletronica.repository.NotaFiscalRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Controller para gerenciar download de DANFE e outros documentos relacionados à NFe
 */
@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor
public class DocumentosController {
    
    @Autowired
    private NotaFiscalRepository notaFiscalRepository;
    
    @Autowired
    private DanfeService danfeService;
    
    /**
     * Endpoint para download de DANFE em PDF
     * GET /api/v1/documentos/{notaFiscalId}/danfe
     */
    @GetMapping("/{notaFiscalId}/danfe")
    public ResponseEntity<byte[]> downloadDanfe(@PathVariable UUID notaFiscalId) {
        
        NotaFiscal nota = notaFiscalRepository.findById(notaFiscalId)
            .orElseThrow(() -> new EntityNotFoundException("Nota Fiscal não encontrada com ID: " + notaFiscalId));
        
        // Gera o PDF da DANFE
        byte[] danfePdf = danfeService.gerarDanfePdf(nota);
        
        // Define o nome do arquivo
        String nomeArquivo = String.format("DANFE_NF_%s_%d.pdf", 
            nota.getId().toString().substring(0, 8), 
            nota.getNumero() != null ? nota.getNumero() : 0);
        
        // Retorna o arquivo para download
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
            .body(danfePdf);
    }
    
    /**
     * Endpoint para download do XML da nota
     * GET /api/v1/documentos/{notaFiscalId}/xml
     */
    @GetMapping("/{notaFiscalId}/xml")
    public ResponseEntity<byte[]> downloadXml(@PathVariable UUID notaFiscalId) {
        
        NotaFiscal nota = notaFiscalRepository.findById(notaFiscalId)
            .orElseThrow(() -> new EntityNotFoundException("Nota Fiscal não encontrada com ID: " + notaFiscalId));
        
        if (nota.getXmlAssinado() == null || nota.getXmlAssinado().isEmpty()) {
            throw new EntityNotFoundException("XML da nota não foi gerado");
        }
        
        byte[] xmlBytes = nota.getXmlAssinado().getBytes();
        String nomeArquivo = String.format("NFe_%s.xml", 
            nota.getChaveAcesso() != null ? nota.getChaveAcesso() : nota.getId());
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
            .header(HttpHeaders.CONTENT_TYPE, "application/xml")
            .body(xmlBytes);
    }
    
    /**
     * Endpoint para visualizar informações da nota em JSON
     * GET /api/v1/documentos/{notaFiscalId}/info
     */
    @GetMapping("/{notaFiscalId}/info")
    public ResponseEntity<NotaFiscal> getInfoNotaFiscal(@PathVariable UUID notaFiscalId) {
        
        NotaFiscal nota = notaFiscalRepository.findById(notaFiscalId)
            .orElseThrow(() -> new EntityNotFoundException("Nota Fiscal não encontrada com ID: " + notaFiscalId));
        
        return ResponseEntity.ok(nota);
    }
}
