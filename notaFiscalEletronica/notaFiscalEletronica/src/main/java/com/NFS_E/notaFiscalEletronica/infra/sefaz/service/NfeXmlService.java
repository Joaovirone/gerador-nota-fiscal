package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NfeXmlService {

    private final NFeConfigImpl config;

    public String gerarXmlAssinado(NotaFiscal nota) {
        try {
            NFNotaFiscal nfe = new NFNotaFiscal();
            
            // 1. Informações da Nota
            NFNotaFiscalInfo info = new NFNotaFiscalInfo();
            info.setIdentificador("NFe" + nota.getChaveAcesso());
            info.setVersao("4.00");

            // 2. Bloco de Identificação (ide)
            info.setIdentificacao(montarIde(nota));

            // 3. Bloco Emitente (emit)
            info.setEmitente(montarEmitente());

            // 4. Bloco Destinatário (dest)
            info.setDestinatario(montarDestinatario(nota));

            // 5. Bloco de Itens (det) - Aqui entra um Loop nos produtos
            info.setItens(montarItens(nota));

            // 6. Bloco de Totais (total)
            info.setTotal(montarTotais(nota));

            nfe.setInfo(info);

            // 7. Assinatura Digital
            return new NFSignature(config).assinarDocumento(nfe.toString());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar/assinar XML: " + e.getMessage());
        }
    }

    private NFInfoIdentificacao montarIde(NotaFiscal nota) {
        NFInfoIdentificacao ide = new NFInfoIdentificacao();
        ide.setUf(config.getCUF());
        ide.setCodigoRandomico(nota.getCodigoNumerico());
        ide.setNaturezaOperacao("VENDA");
        ide.setModelo(NFModelo.NFE);
        ide.setSerie("1");
        ide.setNumeroNota(nota.getNumero().toString());
        ide.setDataHoraEmissao(ZonedDateTime.now());
        ide.setTipo(NFTipo.SAIDA);
        ide.setTipoAmbiente(config.getAmbiente());
        return ide;
    }
    
    
}
