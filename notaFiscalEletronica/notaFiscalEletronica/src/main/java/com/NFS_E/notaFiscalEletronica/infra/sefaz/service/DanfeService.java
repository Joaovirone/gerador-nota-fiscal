package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

/**
 * Serviço para geração de DANFE (Documento Auxiliar de Nota Fiscal Eletrônica) em PDF
 * Gera um documento com as informações essenciais da nota fiscal
 */
@Service
public class DanfeService {
    
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Gera DANFE em PDF com informações simples
     */
    public byte[] gerarDanfePdf(NotaFiscal nota) {
        try (PDDocument document = new PDDocument(); 
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                // Título
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.newLineAtOffset(50, 750);
                content.showText("DANFE - Documento Auxiliar de Nota Fiscal Eletrônica");
                content.endText();
                
                int yPosition = 720;
                
                // Cabeçalho
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, yPosition);
                content.showText("INFORMAÇÕES GERAIS");
                content.endText();
                
                yPosition -= 20;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(50, yPosition);
                content.showText("ID: " + nota.getId());
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(50, yPosition);
                content.showText("Número: " + (nota.getNumero() != null ? nota.getNumero() : "N/A"));
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(50, yPosition);
                content.showText("Série: " + nota.getSerie());
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(50, yPosition);
                content.showText("Status: " + nota.getStatus());
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(50, yPosition);
                content.showText("Destino: " + nota.getNotaFiscalDestino());
                content.endText();
                
                yPosition -= 20;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, yPosition);
                content.showText("CHAVE DE ACESSO");
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, yPosition);
                String chaveAcesso = nota.getChaveAcesso() != null ? nota.getChaveAcesso() : "Não gerada";
                content.showText(chaveAcesso);
                content.endText();
                
                yPosition -= 25;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, yPosition);
                content.showText("VALORES");
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(50, yPosition);
                content.showText("Valor Total: R$ " + formatarMoeda(nota.getValorTotal()));
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(50, yPosition);
                content.showText("Quantidade de Itens: " + (nota.getItens() != null ? nota.getItens().size() : 0));
                content.endText();
                
                // Informações adicionais
                yPosition -= 25;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, yPosition);
                content.showText("INFORMAÇÕES ADICIONAIS");
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(50, yPosition);
                content.showText("Criada em: " + (nota.getDataCriacao() != null ? 
                    DATA_FORMATTER.format(nota.getDataCriacao()) : "N/A"));
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(50, yPosition);
                content.showText("Código Numérico: " + (nota.getCodigoNumerico() != null ? nota.getCodigoNumerico() : "N/A"));
                content.endText();
                
                yPosition -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(50, yPosition);
                content.showText("Protocolo SEFAZ: " + (nota.getProtocoloSefaz() != null ? nota.getProtocoloSefaz() : "N/A"));
                content.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar DANFE em PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Formata um valor BigDecimal como moeda
     */
    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "0,00";
        }
        return String.format("%.2f", valor).replace(".", ",");
    }
}
