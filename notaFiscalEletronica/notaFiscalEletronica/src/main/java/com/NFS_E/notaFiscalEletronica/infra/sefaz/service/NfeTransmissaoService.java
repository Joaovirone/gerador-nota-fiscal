package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.entity.enums.StatusNota;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NfeTransmissaoService {

    private final NfeXmlService nfeXmlService;

    public NotaFiscal transmitir(NotaFiscal notaFiscal) {
        if (notaFiscal == null) {
            throw new IllegalArgumentException("Nota fiscal não pode ser nula para transmissão.");
        }
        if (!StatusNota.PROCESSANDO.equals(notaFiscal.getStatus())) {
            throw new IllegalStateException("Apenas notas em processamento podem ser transmitidas.");
        }

        String xml = nfeXmlService.gerarXmlAssinado(notaFiscal);
        notaFiscal.setXmlAssinado(xml);
        notaFiscal.setProtocoloSefaz("123456789012345");
        notaFiscal.setMotivoSefaz("Nota transmitida em ambiente de homologação simulada.");
        notaFiscal.setStatus(StatusNota.AUTORIZADA);

        return notaFiscal;
    }

    public NotaFiscal cancelarSefaz(NotaFiscal nota, String justificativa) {
        if (nota == null) {
            throw new IllegalArgumentException("Nota fiscal não pode ser nula para cancelamento.");
        }
        if (!StatusNota.AUTORIZADA.equals(nota.getStatus())) {
            throw new IllegalStateException("Somente notas autorizadas podem ser canceladas no SEFAZ.");
        }

        nota.setStatus(StatusNota.CANCELADA);
        nota.setMotivoSefaz(justificativa != null ? justificativa : "Cancelamento de homologação");
        return nota;
    }
}
