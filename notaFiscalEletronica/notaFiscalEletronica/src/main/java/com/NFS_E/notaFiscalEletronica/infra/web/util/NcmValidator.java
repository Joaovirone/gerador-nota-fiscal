package com.NFS_E.notaFiscalEletronica.infra.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Validador para NCM (Nomenclatura Comum do Mercosul)
 * NCM deve ter 8 dígitos
 */
public class NcmValidator {
    
    // Alguns NCMs comuns para validação
    private static final Map<String, String> VALID_NCMS = new HashMap<>();
    
    static {
        VALID_NCMS.put("01012100", "Produtos alimenticios");
        VALID_NCMS.put("15011000", "Banha");
        VALID_NCMS.put("27040090", "Combustivel");
        VALID_NCMS.put("64021900", "Sapatos");
        VALID_NCMS.put("85171110", "Aparelhos telefônicos");
        // Adicione mais NCMs conforme necessário
    }
    
    public static boolean isValid(String ncm) {
        if (ncm == null || ncm.isEmpty()) {
            return false;
        }
        
        // Remove caracteres especiais
        String cleanNcm = ncm.replaceAll("[^0-9]", "");
        
        // Verifica se tem exatamente 8 dígitos
        if (cleanNcm.length() != 8) {
            return false;
        }
        
        // Verifica se é composto apenas por dígitos
        if (!cleanNcm.matches("\\d{8}")) {
            return false;
        }
        
        // Calcula dígito verificador (módulo 11)
        return isValidNcmCheckDigit(cleanNcm);
    }
    
    private static boolean isValidNcmCheckDigit(String ncm) {
        int sum = 0;
        int multiplier = 9;
        
        for (int i = 0; i < 7; i++) {
            sum += Integer.parseInt(String.valueOf(ncm.charAt(i))) * multiplier;
            multiplier--;
        }
        
        int remainder = sum % 11;
        int checkDigit = (remainder == 0 || remainder == 1) ? 0 : 11 - remainder;
        
        return checkDigit == Integer.parseInt(String.valueOf(ncm.charAt(7)));
    }
    
    public static String getDescription(String ncm) {
        return VALID_NCMS.getOrDefault(ncm, "NCM desconhecido");
    }
}
