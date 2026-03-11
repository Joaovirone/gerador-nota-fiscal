package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.infra.sefaz.config.NFeConfigImpl;
import com.fincatto.documentofiscal.nfe400.classes.NFEndereco;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoEmitente;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoIdentificacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.assinatura.NFSignature;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NfeXmlService {

    private final NFeConfigImpl config;

    public String gerarXmlAssinado(NotaFiscal nota) {
        try {
            NFNota nfe = new NFNota();
            
            // 1. Informações da Nota
            NFNotaInfo info = new NFNotaInfo();
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

    private NFNotaInfoIdentificacao montarIde(NotaFiscal nota) {
        NFNotaInfoIdentificacao ide = new NFInfoIdentificacao();
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

    // ... seu método montarIde() ...

    private NFNotaInfoEmitente montarEmitente() {
        NFNotaInfoEmitente emitente = new NFNotaFiscalInfoEmitente();
        emitente.setCnpj("12345678000123"); // CNPJ da sua empresa (apenas números)
        emitente.setRazaoSocial("MINHA EMPRESA DE TESTE LTDA");
        emitente.setRegimeTributario(NFNotaFiscalEmitenteRegimeTributario.SIMPLES_NACIONAL);
        emitente.setInscricaoEstadual("123456789"); // Inscrição Estadual
        
        // Endereço do Emitente
        NFEndereco endereco = new NFEmitenteEndereco();
        endereco.setLogradouro("Rua de Teste");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCodigoMunicipio("3550308"); // Código IBGE da cidade (Ex: São Paulo)
        endereco.setDescricaoMunicipio("SAO PAULO");
        endereco.setUf(config.getCUF());
        endereco.setCep("01000000");
        emitente.setEndereco(endereco);
        
        return emitente;
    }

    private NFNotaInfoDestinatario montarDestinatario(NotaFiscal nota) {
        NFNotaFiscalInfoDestinatario dest = new NFNotaFiscalInfoDestinatario();
        // Como sua entidade ainda não tem dados detalhados do cliente, vamos simular um:
        dest.setCpf("12345678909"); // CPF ou CNPJ do cliente
        dest.setRazaoSocial("CLIENTE DE TESTE");
        dest.setIndicadorIEDestinatario(NFIndicadorIEDestinatario.NAO_CONTRIBUINTE);
        
        // Endereço do Destinatário
        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro("Rua do Cliente");
        endereco.setNumero("456");
        endereco.setBairro("Bairro Novo");
        endereco.setCodigoMunicipio("3550308"); // IBGE
        endereco.setDescricaoMunicipio("SAO PAULO");
        endereco.setUf(config.getCUF());
        endereco.setCep("02000000");
        dest.setEndereco(endereco);
        
        return dest;
    }

    private List<NFNotaFiscalItem> montarItens(NotaFiscal nota) {
        List<NFNotaFiscalItem> itensNfe = new java.util.ArrayList<>();
        int sequencial = 1;

        for (com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal item : nota.getItens()) {
            NFNotaFiscalItem itemSefaz = new NFNotaFiscalItem();
            itemSefaz.setNumeroItem(sequencial++);

            // 1. Dados do Produto
            NFNotaFiscalItemProduto produto = new NFNotaFiscalItemProduto();
            produto.setCodigo(item.getId().toString().substring(0, 10)); // Limite de tamanho
            produto.setDescricao(item.getDescricao());
            produto.setNcm(item.getNcm());
            produto.setCfop(item.getCfop());
            produto.setUnidadeComercial("UN"); 
            produto.setQuantidadeComercial(item.getQuantidade());
            produto.setValorUnitario(item.getValorUnitario());
            produto.setValorTotalBruto(item.getValorTotal()); // Usando o subtotal da sua entidade
            
            produto.setUnidadeTributavel("UN");
            produto.setQuantidadeTributavel(item.getQuantidade());
            produto.setValorUnitarioTributavel(item.getValorUnitario());
            
            itemSefaz.setProduto(produto);

            // 2. Dados do Imposto
            NFNotaFiscalItemImposto imposto = new NFNotaFiscalItemImposto();
            NFImpostoICMS icms = new NFImpostoICMS();
            NFICMS00 icms00 = new NFICMS00(); // Assumindo CST 00 para o teste
            icms00.setOrigem(com.fincatto.documentofiscal.nfe400.classes.NFOrigem.NACIONAL);
            icms00.setSituacaoTributaria(com.fincatto.documentofiscal.nfe400.classes.NFICMSSituacaoTributaria.TRIBUTADA_INTEGRALMENTE);
            icms00.setModalidadeDeterminacaoBC(com.fincatto.documentofiscal.nfe400.classes.NFModalidadeDeterminacaoBCICMS.VALOR_OPERACAO);
            icms00.setValorBaseCalculo(item.getBaseCalculoIcms());
            icms00.setPercentualAliquota(item.getAliquotaIcms());
            icms00.setValorTributo(item.getValorIcms());
            
            icms.setIcms00(icms00);
            imposto.setIcms(icms);
            itemSefaz.setImposto(imposto);

            itensNfe.add(itemSefaz);
        }
        return itensNfe;
    }

    private NFNotaFiscalInfoTotal montarTotais(NotaFiscal nota) {
        NFNotaFiscalInfoTotal total = new NFNotaFiscalInfoTotal();
        NFNotaFiscalInfoICMSTotal icmsTotal = new NFNotaFiscalInfoICMSTotal();
        
        // Aqui você precisa somar as bases e valores de ICMS de todos os itens
        // Por agora, vamos simplificar usando o valor total da nota
        icmsTotal.setValorTotalDosProdutosServicos(nota.getValorTotal());
        icmsTotal.setValorTotalNFe(nota.getValorTotal());
        icmsTotal.setValorTotalFrete(java.math.BigDecimal.ZERO);
        icmsTotal.setValorTotalDesconto(java.math.BigDecimal.ZERO);
        
        // Campos obrigatórios mesmo que zerados
        icmsTotal.setBaseCalculoICMS(java.math.BigDecimal.ZERO);
        icmsTotal.setValorTotalICMS(java.math.BigDecimal.ZERO);
        icmsTotal.setValorICMSDesonerado(java.math.BigDecimal.ZERO);
        icmsTotal.setValorTotalSeguro(java.math.BigDecimal.ZERO);
        icmsTotal.setValorOutrasDespesasAcessorias(java.math.BigDecimal.ZERO);
        icmsTotal.setValorTotalIPI(java.math.BigDecimal.ZERO);
        
        total.setIcmsTotal(icmsTotal);
        return total;
    }
    
    
}
