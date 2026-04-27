package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

@Service
public class DanfeService {

    public byte[] gerarDanfePdf(NotaFiscal nota) {
        throw new UnsupportedOperationException(
            "Geração de DANFE não está disponível sem a dependência JasperReports. " +
            "Adicione net.sf.jasperreports:jasperreports ao pom ou implemente outro gerador."
        );
    }
}
