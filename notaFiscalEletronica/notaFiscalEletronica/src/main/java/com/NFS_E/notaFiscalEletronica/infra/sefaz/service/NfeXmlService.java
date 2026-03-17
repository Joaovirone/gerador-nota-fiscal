package com.NFS_E.notaFiscalEletronica.infra.sefaz.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.infra.sefaz.config.NFeConfigImpl;
import com.fincatto.documentofiscal.nfe400.classes.NFEndereco;
import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoEmitente;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoICMSTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoIdentificacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItem;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImposto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemProduto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTotal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NfeXmlService {

    private final NFeConfigImpl config;

    public String gerarXmlAssinado(NotaFiscal nota) {
        try {
            NFNota nfe = new NFNota();
            
            NFNotaInfo info = new NFNotaInfo();
            info.setIdentificador("NFe" + nota.getChaveAcesso());
            
            info.setIdentificacao(montarIde(nota));

            info.setEmitente(montarEmitente());

            info.setDestinatario(montarDestinatario(nota));

            info.setItens(montarItens(nota));

            info.setTotal(montarTotais(nota));

            nfe.setInfo(info);

            String xmlAssinado = nfe.toString();
            return xmlAssinado;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar XML da NFe: " + e.getMessage(), e);
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
        return ide;
    }

    private NFNotaInfoEmitente montarEmitente() {
        NFNotaInfoEmitente emitente = new NFNotaInfoEmitente();
        emitente.setCnpj("12345678000123"); 
        emitente.setRazaoSocial("EMPRESA DE TESTE LTDA");
        emitente.setRegimeTributario(NFRegimeTributario.SIMPLES_NACIONAL);
        emitente.setInscricaoEstadual("123456789"); 

        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro("Rua Principal");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCodigoMunicipio("3550308"); 
        endereco.setDescricaoMunicipio("SAO PAULO");
        endereco.setUf(config.getCUF());
        endereco.setCep("01000000");
        emitente.setEndereco(endereco);
        
        return emitente;
    }

    private NFNotaInfoDestinatario montarDestinatario(NotaFiscal nota) {
        NFNotaInfoDestinatario dest = new NFNotaInfoDestinatario();
        dest.setCpf("12345678909"); 
        dest.setRazaoSocial("CLIENTE TESTE");
        
        
       
        NFEndereco endereco = new NFEndereco();
        endereco.setLogradouro("Rua do Cliente");
        endereco.setNumero("456");
        endereco.setBairro("Bairro");
        endereco.setCodigoMunicipio("3550308"); 
        endereco.setDescricaoMunicipio("SAO PAULO");
        endereco.setUf(config.getCUF());
        endereco.setCep("02000000");
        dest.setEndereco(endereco);
        
        return dest;
    }

    private List<NFNotaInfoItem> montarItens(NotaFiscal nota) {
        List<NFNotaInfoItem> itensNfe = new ArrayList<>();
        int sequencial = 1;

        for (com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal item : nota.getItens()) {
            NFNotaInfoItem itemSefaz = new NFNotaInfoItem();
            itemSefaz.setNumeroItem(sequencial++);

         
            NFNotaInfoItemProduto produto = new NFNotaInfoItemProduto();
            String codigoProduto = item.getId().toString();
            if (codigoProduto.length() > 10) {
                codigoProduto = codigoProduto.substring(0, 10);
            }
            produto.setCodigo(codigoProduto);
            produto.setDescricao(item.getDescricao());
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

        for (com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal item : nota.getItens()) {
            valorTotalProdutos = valorTotalProdutos.add(item.getValorTotal());
            baseCalculoIcms = baseCalculoIcms.add(item.getBaseCalculoIcms());
            valorTotalIcms = valorTotalIcms.add(item.getValorIcms());
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
