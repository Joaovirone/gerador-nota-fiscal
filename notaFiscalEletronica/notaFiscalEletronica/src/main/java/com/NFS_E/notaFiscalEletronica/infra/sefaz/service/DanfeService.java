package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

@Service
public class DanfeService {

    public byte[] gerarDanfePdf(NotaFiscal nota) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                content.newLineAtOffset(50, 760);
                content.showText("DANFE - Nota Fiscal Eletrônica");
                content.newLineAtOffset(0, -24);
                content.setFont(PDType1Font.HELVETICA, 12);
                content.showText("ID: " + nota.getId());
                content.newLineAtOffset(0, -18);
                content.showText("Destino: " + nota.getNotaFiscalDestino());
                content.newLineAtOffset(0, -18);
                content.showText("Numero: " + (nota.getNumero() != null ? nota.getNumero() : 0));
                content.newLineAtOffset(0, -18);
                content.showText("Status: " + nota.getStatus());
                content.newLineAtOffset(0, -18);
                content.showText("Valor total: R$ " + nota.getValorTotal());
                content.newLineAtOffset(0, -18);
                content.showText("Chave de acesso: " + (nota.getChaveAcesso() != null ? nota.getChaveAcesso() : "não gerada"));
                content.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar DANFE em PDF: " + e.getMessage(), e);
        }
    }
}
