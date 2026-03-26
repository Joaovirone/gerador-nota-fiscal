package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.springframework.stereotype.Service;
import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

@Service
public class DanfeService {

    public byte[] gerarDanfePdf(NotaFiscal nota) {
        try {
            if (nota.getXmlProcessado() == null) {
                throw new RuntimeException("Nota ainda não possui XML processado pela SEFAZ.");
            }

            InputStream xmlInputStream = new ByteArrayInputStream(nota.getXmlProcessado().getBytes("UTF-8"));

            JRXmlDataSource xmlDataSource = new JRXmlDataSource(xmlInputStream, "/nfeProc/NFe/infNFe/det");

            InputStream templateJasper = getClass().getResourceAsStream("/relatorios/danfe.jasper");

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateJasper, null, xmlDataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar DANFE PDF: " + e.getMessage());
        }
    }
}