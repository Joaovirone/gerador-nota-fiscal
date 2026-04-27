package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

@Service
public class NfeTransmissaoService {

    public NotaFiscal transmitir(NotaFiscal notaFiscal) {
        throw new UnsupportedOperationException(
            "Transmissão SEFAZ não está disponível nesta versão. " +
            "Implemente o conector SEFAZ ou ajuste as dependências da biblioteca nfe."
        );
    }

    public NotaFiscal cancelarSefaz(NotaFiscal nota, String justificativa) {
        throw new UnsupportedOperationException(
            "Cancelamento SEFAZ não está disponível nesta versão. " +
            "Implemente o conector SEFAZ ou ajuste as dependências da biblioteca nfe."
        );
    }
}
