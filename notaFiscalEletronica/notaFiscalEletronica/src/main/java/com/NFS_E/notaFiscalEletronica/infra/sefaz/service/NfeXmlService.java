package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.infra.sefaz.config.NFeConfigImpl;
import com.fincatto.documentofiscal.DFConfig;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFEndereco;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFOrigem;
import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
import com.fincatto.documentofiscal.nfe400.classes.nota.assinatura.NFSignature;


import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoEmitente;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoICMSTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoIdentificacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItem;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImposto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS00;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemProduto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.assinatura.NFSignature;
import com.fincatto.documentofiscal.utils.DFAssinaturaDigital; 

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NfeXmlService {

    private final NFeConfigImpl config;

    public String gerarXmlAssinado(NotaFiscal nota) {
        try {
            NFNota nfe = new NFNota();
            NFNotaInfo info = new NFNotaInfo();

            info.setVersao(new BigDecimal("4.00")); 

            info.setIdentificacao(montarIde(nota));
            
            // Agora os métodos esperam receber a nota para buscar os dados dinâmicos
            info.setEmitente(montarEmitente(nota));
            info.setDestinatario(montarDestinatario(nota));
            
            info.setItens(montarItens(nota));
            info.setTotal(montarTotais(nota));

            String chaveGerada = info.getChaveAcesso();
            info.setIdentificador(chaveGerada);
            
            nota.setChaveAcesso(chaveGerada.replace("NFe", "")); 

            nfe.setInfo(info);

            return new DFAssinaturaDigital(config).assinarDocumento(nfe.toString());
            
        }catch (Exception e) {
            throw new RuntimeException("Erro ao gerar/assinar XML da NFe: " + e.getMessage(), e);
        }
}

    private NFNotaInfoIdentificacao montarIde(NotaFiscal nota) {
        NFNotaInfoIdentificacao ide = new NFNotaInfoIdentificacao();
        ide.setUf(config.getCUF());
        ide.setCodigoRandomico(nota.getCodigoNumerico()); 
        ide.setNaturezaOperacao("VENDA");
        ide.setSerie(nota.getSerie().toString());
        ide.setNumeroNota(nota.getNumero().toString());
        ide.setDataHoraEmissao(ZonedDateTime.now());
        ide.setTipo(NFTipo.SAIDA);
        ide.setAmbiente(config.getAmbiente());
        
        // ATENÇÃO: Substitua por um campo dinâmico da sua entidade Empresa (IBGE da cidade origem)
        ide.setCodigoMunicipio("3550308"); 
        
        ide.setFinalidade(NFFinalidade.NORMAL);
        
        // CORREÇÕES APLICADAS AQUI:
        ide.setTipoEmissao(NFTipoEmissao.EMISSAO_NORMAL); 
        ide.setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE); 
        ide.setVersaoEmissor("1.0"); 
        
        return ide;
    }

    private NFNotaInfoEmitente montarEmitente(NotaFiscal nota) {
        NFNotaInfoEmitente emitenteSefaz = new NFNotaInfoEmitente();
        
        // OBS: Você precisará criar a relação Emitente/Empresa na sua entidade NotaFiscal
        // Exemplo: emitenteSefaz.setCnpj(nota.getEmitente().getCnpj());
        
        // Substitua as strings pelos getters da sua entidade Emitente/Empresa
        emitenteSefaz.setCnpj("SUBSTITUA_PELO_CNPJ_DINAMICO");
        emitenteSefaz.setRazaoSocial("SUBSTITUA_PELA_RAZAO_SOCIAL");
        emitenteSefaz.setRegimeTributario(NFRegimeTributario.SIMPLES_NACIONAL);
        emitenteSefaz.setInscricaoEstadual("SUBSTITUA_PELA_IE");

        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro("SUBSTITUA_RUA");
        endereco.setNumero("SUBSTITUA_NUMERO");
        endereco.setBairro("SUBSTITUA_BAIRRO");
        endereco.setCodigoMunicipio("3550308"); // IBGE Dinâmico
        endereco.setDescricaoMunicipio("SUBSTITUA_NOME_CIDADE");
        endereco.setUf(config.getCUF());
        endereco.setCep("01000000"); // CEP Dinâmico
        
        emitenteSefaz.setEndereco(endereco);

        return emitenteSefaz;
    }

    private NFNotaInfoDestinatario montarDestinatario(NotaFiscal nota) {
        NFNotaInfoDestinatario destSefaz = new NFNotaInfoDestinatario();
        
        // OBS: Você precisará criar a relação Cliente/Destinatário na sua entidade NotaFiscal
        // Exemplo: destSefaz.setCpf(nota.getCliente().getCpfCnpj());
        
        destSefaz.setCpf("SUBSTITUA_PELO_CPF_CNPJ_CLIENTE");
        destSefaz.setRazaoSocial("SUBSTITUA_PELO_NOME_CLIENTE");

        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro("SUBSTITUA_RUA_CLIENTE");
        endereco.setNumero("SUBSTITUA_NUMERO_CLIENTE");
        endereco.setBairro("SUBSTITUA_BAIRRO_CLIENTE");
        endereco.setCodigoMunicipio("3550308"); // IBGE Dinâmico
        endereco.setDescricaoMunicipio("SUBSTITUA_CIDADE_CLIENTE");
        endereco.setUf(config.getCUF()); // Cuidado, destinatário pode ser de outro UF
        endereco.setCep("02000000"); // CEP Dinâmico
        
        destSefaz.setEndereco(endereco);

        return destSefaz;
    }

    private List<NFNotaInfoItem> montarItens(NotaFiscal nota) {
        List<NFNotaInfoItem> itensNfe = new ArrayList<>();
        int sequencial = 1;

        if (nota.getItens() == null) return itensNfe;

        for (com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal item : nota.getItens()) {
            NFNotaInfoItem itemSefaz = new NFNotaInfoItem();
            itemSefaz.setNumeroItem(sequencial++);

            NFNotaInfoItemProduto produto = new NFNotaInfoItemProduto();
            
            String codigoProduto = item.getId() != null ? item.getId().toString() : "001";
            if (codigoProduto.length() > 10) {
                codigoProduto = codigoProduto.substring(0, 10);
            }
            
            produto.setCodigo(codigoProduto);
            produto.setDescricao(item.getDescricao() != null ? item.getDescricao() : "Produto sem descricao");
            produto.setNcm(item.getNcm());
            produto.setCfop(item.getCfop());
            produto.setUnidadeComercial("UN");
            produto.setQuantidadeComercial(item.getQuantidade());
            produto.setValorUnitario(item.getValorUnitario());
            produto.setValorTotalBruto(item.getValorTotal());
            produto.setUnidadeTributavel("UN");
            produto.setQuantidadeTributavel(item.getQuantidade());
            produto.setValorUnitarioTributavel(item.getValorUnitario());
            
            itemSefaz.setProduto(produto);

            montarImpostosItem(itemSefaz, item);

            itensNfe.add(itemSefaz);
        }
        return itensNfe;
    }

    private void montarImpostosItem(NFNotaInfoItem itemSefaz, com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal item) {
        try {
            NFNotaInfoItemImposto imposto = new NFNotaInfoItemImposto();
            
            NFNotaInfoItemImpostoICMS icms = new NFNotaInfoItemImpostoICMS();
            NFNotaInfoItemImpostoICMS00 icms00 = new NFNotaInfoItemImpostoICMS00();
            
            icms00.setOrigem(NFOrigem.NACIONAL);
            icms00.setSituacaoTributaria(NFNotaInfoImpostoTributacaoICMS.TRIBUTACAO_INTEGRALMENTE); 
            
            // CORREÇÃO: O método correto chama-se setModalidadeBC
            icms00.setModalidadeBCICMS(NFNotaInfoItemModalidadeBCICMS.VALOR_OPERACAO);
            
            icms00.setValorBaseCalculo(item.getBaseCalculoIcms() != null ? item.getBaseCalculoIcms() : BigDecimal.ZERO);
            icms00.setPercentualAliquota(item.getAliquotaIcms() != null ? item.getAliquotaIcms() : BigDecimal.ZERO);
            icms00.setValorTributo(item.getValorIcms() != null ? item.getValorIcms() : BigDecimal.ZERO);
            
            icms.setIcms00(icms00);
            imposto.setIcms(icms);

            itemSefaz.setImposto(imposto);
        } catch (Exception e) {
            System.err.println("Aviso: Erro ao configurar impostos do item: " + e.getMessage());
        }
    }

    private NFNotaInfoTotal montarTotais(NotaFiscal nota) {
        NFNotaInfoTotal total = new NFNotaInfoTotal();
        NFNotaInfoICMSTotal icmsTotal = new NFNotaInfoICMSTotal();

        BigDecimal valorTotalProdutos = BigDecimal.ZERO;
        BigDecimal baseCalculoIcms = BigDecimal.ZERO;
        BigDecimal valorTotalIcms = BigDecimal.ZERO;

        if (nota.getItens() != null) {
            for (com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal item : nota.getItens()) {
                if(item.getValorTotal() != null) valorTotalProdutos = valorTotalProdutos.add(item.getValorTotal());
                if(item.getBaseCalculoIcms() != null) baseCalculoIcms = baseCalculoIcms.add(item.getBaseCalculoIcms());
                if(item.getValorIcms() != null) valorTotalIcms = valorTotalIcms.add(item.getValorIcms());
            }
        }

        icmsTotal.setValorTotalDosProdutosServicos(valorTotalProdutos);
        icmsTotal.setValorTotalNFe(valorTotalProdutos.add(valorTotalIcms));
        icmsTotal.setValorTotalFrete(BigDecimal.ZERO);
        icmsTotal.setValorTotalDesconto(BigDecimal.ZERO);

        icmsTotal.setBaseCalculoICMS(baseCalculoIcms);
        icmsTotal.setValorTotalICMS(valorTotalIcms);
        icmsTotal.setValorICMSDesonerado(BigDecimal.ZERO);
        icmsTotal.setValorTotalSeguro(BigDecimal.ZERO);
        icmsTotal.setOutrasDespesasAcessorias(BigDecimal.ZERO);
        icmsTotal.setValorTotalIPI(BigDecimal.ZERO);
        
        total.setIcmsTotal(icmsTotal);
        return total;
    }
}