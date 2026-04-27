package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.infra.sefaz.config.NFeConfigImpl;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFEndereco;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFOrigem;
import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
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
import com.fincatto.documentofiscal.utils.DFAssinaturaDigital;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NfeXmlService {

    private final NFeConfigImpl config;

    public String gerarXmlAssinado(NotaFiscal nota) {
        try {
            String xml = gerarXml(nota);
            if (config.getCertificadoKeyStore() != null) {
                return new DFAssinaturaDigital(config).assinarDocumento(xml);
            }
            return xml;
        } catch (IllegalStateException e) {
            return gerarXml(nota);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar/assinar XML da NFe: " + e.getMessage(), e);
        }
    }

    private String gerarXml(NotaFiscal nota) {
        NFNota nfe = new NFNota();
        NFNotaInfo info = new NFNotaInfo();

        info.setVersao(new BigDecimal("4.00"));
        info.setIdentificacao(montarIde(nota));
        info.setEmitente(montarEmitente(nota));
        info.setDestinatario(montarDestinatario(nota));
        info.setItens(montarItens(nota));
        info.setTotal(montarTotais(nota));

        String chaveGerada = gerarChaveAcesso(nota);
        info.setIdentificador(chaveGerada);
        nota.setChaveAcesso(chaveGerada);

        nfe.setInfo(info);
        return nfe.toString();
    }

    private String gerarChaveAcesso(NotaFiscal nota) {
        if (nota.getChaveAcesso() != null && !nota.getChaveAcesso().isBlank()) {
            return nota.getChaveAcesso();
        }

        String codigoUf = String.valueOf(config.getCUF().getCodigoIbge());
        if (codigoUf.length() == 1) {
            codigoUf = "0" + codigoUf;
        }

        String ano = String.valueOf(ZonedDateTime.now().getYear() % 100);
        if (ano.length() == 1) {
            ano = "0" + ano;
        }

        String mes = String.format("%02d", ZonedDateTime.now().getMonthValue());
        String cnpj = "11111111000191";
        String modelo = "55";
        String serie = String.format("%03d", nota.getSerie());
        String numero = String.format("%09d", nota.getNumero() != null ? nota.getNumero() : 1L);
        String codigoNumerico = nota.getCodigoNumerico();
        String tipoEmissao = "1";
        String digitoVerificador = "0";

        String chave = codigoUf + ano + mes + cnpj + modelo + serie + numero + codigoNumerico + tipoEmissao + digitoVerificador;
        return chave.length() > 44 ? chave.substring(0, 44) : chave;
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
        ide.setCodigoMunicipio("3550308");
        ide.setFinalidade(NFFinalidade.NORMAL);
        ide.setTipoEmissao(NFTipoEmissao.EMISSAO_NORMAL);
        ide.setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);
        ide.setVersaoEmissor("1.0");
        return ide;
    }

    private NFNotaInfoEmitente montarEmitente(NotaFiscal nota) {
        NFNotaInfoEmitente emitenteSefaz = new NFNotaInfoEmitente();
        emitenteSefaz.setCnpj("11111111000191");
        emitenteSefaz.setRazaoSocial("Empresa Exemplo Ltda");
        emitenteSefaz.setRegimeTributario(NFRegimeTributario.SIMPLES_NACIONAL);
        emitenteSefaz.setInscricaoEstadual("123456789");

        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro("Rua do Exemplo");
        endereco.setNumero("100");
        endereco.setBairro("Centro");
        endereco.setCodigoMunicipio("3550308");
        endereco.setDescricaoMunicipio("São Paulo");
        endereco.setUf(config.getCUF());
        endereco.setCep("01000000");
        emitenteSefaz.setEndereco(endereco);

        return emitenteSefaz;
    }

    private NFNotaInfoDestinatario montarDestinatario(NotaFiscal nota) {
        NFNotaInfoDestinatario destSefaz = new NFNotaInfoDestinatario();
        destSefaz.setCpf("00000000000");
        destSefaz.setRazaoSocial(nota.getNotaFiscalDestino() != null ? nota.getNotaFiscalDestino() : "Cliente sem nome");

        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro("Rua do Cliente");
        endereco.setNumero("1");
        endereco.setBairro("Bairro Cliente");
        endereco.setCodigoMunicipio("3550308");
        endereco.setDescricaoMunicipio("São Paulo");
        endereco.setUf(config.getCUF());
        endereco.setCep("02000000");
        destSefaz.setEndereco(endereco);

        return destSefaz;
    }

    private List<NFNotaInfoItem> montarItens(NotaFiscal nota) {
        List<NFNotaInfoItem> itensNfe = new ArrayList<>();
        int sequencial = 1;
        if (nota.getItens() == null) {
            return itensNfe;
        }

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
                if (item.getValorTotal() != null) {
                    valorTotalProdutos = valorTotalProdutos.add(item.getValorTotal());
                }
                if (item.getBaseCalculoIcms() != null) {
                    baseCalculoIcms = baseCalculoIcms.add(item.getBaseCalculoIcms());
                }
                if (item.getValorIcms() != null) {
                    valorTotalIcms = valorTotalIcms.add(item.getValorIcms());
                }
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
