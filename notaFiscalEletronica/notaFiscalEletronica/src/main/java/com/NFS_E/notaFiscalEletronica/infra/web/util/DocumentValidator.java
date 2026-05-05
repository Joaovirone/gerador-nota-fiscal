package com.NFS_E.notaFiscalEletronica.infra.web.util;

/**
 * Validador para CPF e CNPJ
 */
public class DocumentValidator {
    
    /**
     * Valida CPF
     * @param cpf CPF em formato "12345678901" ou "123.456.789-01"
     * @return true se válido
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }
        
        // Remove caracteres especiais
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        // Deve ter 11 dígitos
        if (cleanCpf.length() != 11) {
            return false;
        }
        
        // Não pode ser uma sequência de números iguais
        if (cleanCpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Calcula primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Integer.parseInt(String.valueOf(cleanCpf.charAt(i))) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        firstDigit = (firstDigit >= 10) ? 0 : firstDigit;
        
        // Calcula segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Integer.parseInt(String.valueOf(cleanCpf.charAt(i))) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        secondDigit = (secondDigit >= 10) ? 0 : secondDigit;
        
        // Verifica se os dígitos verificadores correspondem
        return (firstDigit == Integer.parseInt(String.valueOf(cleanCpf.charAt(9)))) &&
               (secondDigit == Integer.parseInt(String.valueOf(cleanCpf.charAt(10))));
    }
    
    /**
     * Valida CNPJ
     * @param cnpj CNPJ em formato "12345678000195" ou "12.345.678/0001-95"
     * @return true se válido
     */
    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null || cnpj.isEmpty()) {
            return false;
        }
        
        // Remove caracteres especiais
        String cleanCnpj = cnpj.replaceAll("[^0-9]", "");
        
        // Deve ter 14 dígitos
        if (cleanCnpj.length() != 14) {
            return false;
        }
        
        // Não pode ser uma sequência de números iguais
        if (cleanCnpj.matches("(\\d)\\1{13}")) {
            return false;
        }
        
        // Calcula primeiro dígito verificador
        int[] multiplier1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Integer.parseInt(String.valueOf(cleanCnpj.charAt(i))) * multiplier1[i];
        }
        int firstDigit = 11 - (sum % 11);
        firstDigit = (firstDigit >= 10) ? 0 : firstDigit;
        
        // Calcula segundo dígito verificador
        int[] multiplier2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += Integer.parseInt(String.valueOf(cleanCnpj.charAt(i))) * multiplier2[i];
        }
        int secondDigit = 11 - (sum % 11);
        secondDigit = (secondDigit >= 10) ? 0 : secondDigit;
        
        // Verifica se os dígitos verificadores correspondem
        return (firstDigit == Integer.parseInt(String.valueOf(cleanCnpj.charAt(12)))) &&
               (secondDigit == Integer.parseInt(String.valueOf(cleanCnpj.charAt(13))));
    }
    
    /**
     * Formata CPF para "123.456.789-01"
     */
    public static String formatCpf(String cpf) {
        String clean = cpf.replaceAll("[^0-9]", "");
        if (clean.length() != 11) {
            return cpf;
        }
        return String.format("%s.%s.%s-%s", 
            clean.substring(0, 3),
            clean.substring(3, 6),
            clean.substring(6, 9),
            clean.substring(9, 11));
    }
    
    /**
     * Formata CNPJ para "12.345.678/0001-95"
     */
    public static String formatCnpj(String cnpj) {
        String clean = cnpj.replaceAll("[^0-9]", "");
        if (clean.length() != 14) {
            return cnpj;
        }
        return String.format("%s.%s.%s/%s-%s",
            clean.substring(0, 2),
            clean.substring(2, 5),
            clean.substring(5, 8),
            clean.substring(8, 12),
            clean.substring(12, 14));
    }
}
