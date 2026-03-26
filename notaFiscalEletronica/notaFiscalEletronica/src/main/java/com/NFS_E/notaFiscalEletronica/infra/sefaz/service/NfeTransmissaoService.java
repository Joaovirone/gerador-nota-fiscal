package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.entity.enums.StatusNota;
import com.NFS_E.notaFiscalEletronica.infra.sefaz.config.NFeConfigImpl;

import com.fincatto.documentofiscal.nfe.WSFacade;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvio;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.utils.DFPersister;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NfeTransmissaoService {

    private final NFeConfigImpl config;
    private final NfeXmlService nfeXmlService;

    public NotaFiscal transmitir(NotaFiscal notaFiscal) {
        try {
            String xmlAssinado = nfeXmlService.gerarXmlAssinado(notaFiscal);
            notaFiscal.setXmlAssinado(xmlAssinado);

            NFNota nfNota = new DFPersister().read(NFNota.class, xmlAssinado);

            NFLoteEnvio lote = new NFLoteEnvio();
            lote.setIdLote("1");
            lote.setVersao("4.00");
            lote.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_SINCRONO); 
            lote.setNotas(Collections.singletonList(nfNota));

            WSFacade wsFacade = new WSFacade(config);
            System.out.println("Enviando lote para a SEFAZ...");
            NFLoteEnvioRetorno retorno = wsFacade.enviaLote(lote);

            processarRetornoSefaz(retorno, notaFiscal);

            return notaFiscal;

        } catch (Exception e) {
            notaFiscal.setStatus(StatusNota.ERRO_TRANSMISSAO);
            notaFiscal.setMotivoSefaz("Erro interno: " + e.getMessage());
            throw new RuntimeException("Falha catastrófica ao transmitir NFe: " + e.getMessage(), e);
        }
    }

    private void processarRetornoSefaz(NFLoteEnvioRetorno retorno, NotaFiscal nota) {

        if ("104".equals(retorno.getStatus()) && retorno.getProtocoloRetorno() != null) {

            var protocoloInfo = retorno.getProtocoloRetorno().getProtocoloInfo();
            String statusSefaz = protocoloInfo.getStatus();
            
            nota.setMotivoSefaz(statusSefaz + " - " + protocoloInfo.getMotivo());

            if ("100".equals(statusSefaz)) {
                nota.setStatus(StatusNota.AUTORIZADA);
                nota.setProtocoloSefaz(protocoloInfo.getNumeroProtocolo());
                System.out.println("NOTA AUTORIZADA! Protocolo: " + nota.getProtocoloSefaz());
            } 
            else if ("110".equals(statusSefaz) || "301".equals(statusSefaz)) {
                nota.setStatus(StatusNota.CANCELADA);
                System.err.println("NOTA CANCELADA: " + protocoloInfo.getMotivo());
            }
            else {
                nota.setStatus(StatusNota.REJEITADA);
                System.err.println("NOTA REJEITADA: " + protocoloInfo.getMotivo());
            }
        } else {
            nota.setMotivoSefaz(retorno.getStatus() + " - " + retorno.getMotivo());
            nota.setStatus(StatusNota.REJEITADA);
        }
    }
}