package com.NFS_E.notaFiscalEletronica.infra.web.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validador para CST ICMS (Código de Situação Tributária)
 * e alíquotas por UF
 */
public class IcmsValidator {
    
    // CSTs válidos para ICMS
    private static final Set<String> VALID_CST_ICMS = new HashSet<>();
    
    // Alíquotas de ICMS por UF
    private static final Map<String, BigDecimal> ICMS_ALIQUOTS = new HashMap<>();
    
    static {
        // Códigos de Situação Tributária - ICMS
        VALID_CST_ICMS.add("00"); // Tributada integralmente
        VALID_CST_ICMS.add("10"); // Tributada e com cobrança do ICMS por ST
        VALID_CST_ICMS.add("20"); // Com redução de base de cálculo
        VALID_CST_ICMS.add("30"); // Isenta ou não tributada e com cobrança do ICMS por ST
        VALID_CST_ICMS.add("40"); // Isenta
        VALID_CST_ICMS.add("41"); // Não tributada
        VALID_CST_ICMS.add("50"); // Suspensão
        VALID_CST_ICMS.add("51"); // Diferimento
        VALID_CST_ICMS.add("60"); // ICMS cobrado anteriormente por ST
        VALID_CST_ICMS.add("70"); // Com redução de base de cálculo e cobrança do ICMS por ST
        VALID_CST_ICMS.add("90"); // Outras
        
        // Alíquotas de ICMS por UF (exemplos)
        ICMS_ALIQUOTS.put("AC", BigDecimal.valueOf(17.0)); // Acre
        ICMS_ALIQUOTS.put("AL", BigDecimal.valueOf(17.0)); // Alagoas
        ICMS_ALIQUOTS.put("AP", BigDecimal.valueOf(18.0)); // Amapá
        ICMS_ALIQUOTS.put("AM", BigDecimal.valueOf(18.0)); // Amazonas
        ICMS_ALIQUOTS.put("BA", BigDecimal.valueOf(18.0)); // Bahia
        ICMS_ALIQUOTS.put("CE", BigDecimal.valueOf(17.0)); // Ceará
        ICMS_ALIQUOTS.put("DF", BigDecimal.valueOf(18.0)); // Distrito Federal
        ICMS_ALIQUOTS.put("ES", BigDecimal.valueOf(17.0)); // Espírito Santo
        ICMS_ALIQUOTS.put("GO", BigDecimal.valueOf(17.0)); // Goiás
        ICMS_ALIQUOTS.put("MA", BigDecimal.valueOf(18.0)); // Maranhão
        ICMS_ALIQUOTS.put("MT", BigDecimal.valueOf(17.0)); // Mato Grosso
        ICMS_ALIQUOTS.put("MS", BigDecimal.valueOf(17.0)); // Mato Grosso do Sul
        ICMS_ALIQUOTS.put("MG", BigDecimal.valueOf(18.0)); // Minas Gerais
        ICMS_ALIQUOTS.put("PA", BigDecimal.valueOf(17.0)); // Pará
        ICMS_ALIQUOTS.put("PB", BigDecimal.valueOf(18.0)); // Paraíba
        ICMS_ALIQUOTS.put("PR", BigDecimal.valueOf(18.0)); // Paraná
        ICMS_ALIQUOTS.put("PE", BigDecimal.valueOf(17.0)); // Pernambuco
        ICMS_ALIQUOTS.put("PI", BigDecimal.valueOf(17.0)); // Piauí
        ICMS_ALIQUOTS.put("RJ", BigDecimal.valueOf(20.0)); // Rio de Janeiro
        ICMS_ALIQUOTS.put("RN", BigDecimal.valueOf(17.0)); // Rio Grande do Norte
        ICMS_ALIQUOTS.put("RS", BigDecimal.valueOf(17.0)); // Rio Grande do Sul
        ICMS_ALIQUOTS.put("RO", BigDecimal.valueOf(17.5)); // Rondônia
        ICMS_ALIQUOTS.put("RR", BigDecimal.valueOf(15.0)); // Roraima
        ICMS_ALIQUOTS.put("SC", BigDecimal.valueOf(17.0)); // Santa Catarina
        ICMS_ALIQUOTS.put("SP", BigDecimal.valueOf(18.0)); // São Paulo
        ICMS_ALIQUOTS.put("SE", BigDecimal.valueOf(17.0)); // Sergipe
        ICMS_ALIQUOTS.put("TO", BigDecimal.valueOf(17.0)); // Tocantins
    }
    
    public static boolean isValidCstIcms(String cst) {
        if (cst == null || cst.isEmpty()) {
            return false;
        }
        
        String cleanCst = cst.replaceAll("[^0-9]", "");
        
        if (cleanCst.length() != 2) {
            return false;
        }
        
        return VALID_CST_ICMS.contains(cleanCst);
    }
    
    public static BigDecimal getDefaultAliquot(String uf) {
        if (uf == null || uf.isEmpty()) {
            return BigDecimal.valueOf(18.0); // Padrão
        }
        
        return ICMS_ALIQUOTS.getOrDefault(uf.toUpperCase(), BigDecimal.valueOf(18.0));
    }
    
    public static boolean isValidAliquot(BigDecimal aliquot) {
        if (aliquot == null || aliquot.compareTo(BigDecimal.ZERO) < 0 
            || aliquot.compareTo(BigDecimal.valueOf(100)) > 0) {
            return false;
        }
        
        return true;
    }
}
