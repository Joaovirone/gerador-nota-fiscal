package com.NFS_E.notaFiscalEletronica.infra.web.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Validador para CFOP (Código Fiscal de Operações e Prestações)
 * CFOP deve ter 4 dígitos
 */
public class CfopValidator {
    
    // CFOPs válidos para operações de saída
    private static final Set<String> VALID_CFOPS_OUT = new HashSet<>();
    // CFOPs válidos para operações de entrada
    private static final Set<String> VALID_CFOPS_IN = new HashSet<>();
    
    static {
        // Operações de saída (começam com 5 ou 6)
        VALID_CFOPS_OUT.add("5102"); // Venda de produção do estabelecimento
        VALID_CFOPS_OUT.add("5103"); // Venda de produção do estabelecimento (retorno cliente)
        VALID_CFOPS_OUT.add("5104"); // Venda de produção do estabelecimento (remessa cliente)
        VALID_CFOPS_OUT.add("5105"); // Venda de produção do estabelecimento (devolução cliente)
        VALID_CFOPS_OUT.add("5106"); // Venda de produção do estabelecimento (remessa beneficiamento)
        VALID_CFOPS_OUT.add("5202"); // Venda de mercadoria adquirida
        VALID_CFOPS_OUT.add("5405"); // Fornecimento de energia elétrica
        VALID_CFOPS_OUT.add("5503"); // Prestação de serviço de transporte
        
        // Operações de entrada (começam com 1 ou 2)
        VALID_CFOPS_IN.add("1102"); // Compra para revenda
        VALID_CFOPS_IN.add("1201"); // Compra de matéria-prima
        VALID_CFOPS_IN.add("1556"); // Compra para industrialização
    }
    
    public static boolean isValid(String cfop) {
        if (cfop == null || cfop.isEmpty()) {
            return false;
        }
        
        String cleanCfop = cfop.replaceAll("[^0-9]", "");
        
        if (cleanCfop.length() != 4) {
            return false;
        }
        
        if (!cleanCfop.matches("\\d{4}")) {
            return false;
        }
        
        // Valida se é um CFOP conhecido
        return VALID_CFOPS_OUT.contains(cleanCfop) || VALID_CFOPS_IN.contains(cleanCfop);
    }
    
    public static boolean isValidOut(String cfop) {
        if (cfop == null || cfop.isEmpty()) {
            return false;
        }
        String cleanCfop = cfop.replaceAll("[^0-9]", "");
        return VALID_CFOPS_OUT.contains(cleanCfop);
    }
    
    public static boolean isValidIn(String cfop) {
        if (cfop == null || cfop.isEmpty()) {
            return false;
        }
        String cleanCfop = cfop.replaceAll("[^0-9]", "");
        return VALID_CFOPS_IN.contains(cleanCfop);
    }
}
