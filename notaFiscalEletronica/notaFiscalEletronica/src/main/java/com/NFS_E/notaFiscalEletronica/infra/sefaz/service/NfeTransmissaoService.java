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

        if ("100".equals(statusSefaz)) {
            nota.setStatus(StatusNota.AUTORIZADA);
            nota.setProtocoloSefaz(protocoloInfo.getNumeroProtocolo());

            // --- NOVO: GERANDO O XML COM VALIDADE JURÍDICA ---
            try {
                NFNotaProcessada notaProcessada = new NFNotaProcessada();
                notaProcessada.setVersao(new BigDecimal("4.00"));

                // nfNota é o objeto gerado no início do transmitir()
                // retorno.getProtocoloRetorno() é a resposta da SEFAZ
                notaProcessada.setNota(nfNota);
                notaProcessada.setProtocolo(retorno.getProtocoloRetorno());

                // Salva o XML final no banco de dados!
                nota.setXmlProcessado(notaProcessada.toString());
            } catch (Exception e) {
                System.err.println("Erro ao montar procNfe: " + e.getMessage());
            }

            System.out.println("NOTA AUTORIZADA! Protocolo: " + nota.getProtocoloSefaz());
        }

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


    public NotaFiscal cancelarSefaz(NotaFiscal nota, String justificativa) {
        try {
            if (justificativa.length() < 15) throw new RuntimeException("Justificativa deve ter no mínimo 15 caracteres.");

            NFInfoEventoCancelamento info = new NFInfoEventoCancelamento();
            info.setAmbiente(config.getAmbiente());
            info.setChave(nota.getChaveAcesso());
            info.setCnpj("12345678000123");
            info.setDataHoraEvento(ZonedDateTime.now());
            info.setNumeroProtocolo(nota.getProtocoloSefaz());
            info.setOrgao(config.getCUF());
            info.setJustificativa(justificativa);
            info.setNumeroSequencialEvento(1);

            NFEventoCancelamento evento = new NFEventoCancelamento();
            evento.setInfoEvento(info);
            evento.setVersao("1.00");

            NFEnviaEventoCancelamento enviaCancelamento = new NFEnviaEventoCancelamento();
            enviaCancelamento.setEvento(Collections.singletonList(evento));
            enviaCancelamento.setIdLote("1");
            enviaCancelamento.setVersao("1.00");

            System.out.println("Enviando evento de Cancelamento para a SEFAZ...");
            WSFacade wsFacade = new WSFacade(config);
            NFEnviaEventoRetorno retorno = wsFacade.cancelaNota(enviaCancelamento);

            if ("135".equals(retorno.getRetornoEventos().get(0).getInfoEventoRetorno().getCodigoStatus())) {
                nota.setStatus(StatusNota.CANCELADA);
                nota.setMotivoSefaz("Nota Cancelada com sucesso.");
            } else {
                throw new RuntimeException("Erro ao cancelar: " + retorno.getRetornoEventos().get(0).getInfoEventoRetorno().getMotivo());
            }

            return nota;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar cancelamento para SEFAZ: " + e.getMessage(), e);
        }

}