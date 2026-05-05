package com.NFS_E.notaFiscalEletronica.infra.web.util;

import java.math.BigDecimal;

import com.NFS_E.notaFiscalEletronica.infra.exception.ValidationException;

/**
 * Serviço centralizado de validação de regras de negócio
 */
public class BusinessValidationService {
    
    /**
     * Valida dados de item da NFe
     */
    public static void validateItemNotaFiscal(String ncm, String cfop, String cstIcms, 
                                               BigDecimal aliquotaIcms, String uf) {
        
        // Valida NCM
        if (ncm == null || ncm.isEmpty()) {
            throw new ValidationException("NCM é obrigatório");
        }
        if (!NcmValidator.isValid(ncm)) {
            throw new ValidationException("NCM inválido: " + ncm);
        }
        
        // Valida CFOP
        if (cfop == null || cfop.isEmpty()) {
            throw new ValidationException("CFOP é obrigatório");
        }
        if (!CfopValidator.isValid(cfop)) {
            throw new ValidationException("CFOP inválido: " + cfop);
        }
        
        // Valida CST ICMS
        if (cstIcms == null || cstIcms.isEmpty()) {
            throw new ValidationException("CST ICMS é obrigatório");
        }
        if (!IcmsValidator.isValidCstIcms(cstIcms)) {
            throw new ValidationException("CST ICMS inválido: " + cstIcms);
        }
        
        // Valida alíquota ICMS
        if (aliquotaIcms == null) {
            throw new ValidationException("Alíquota ICMS é obrigatória");
        }
        if (!IcmsValidator.isValidAliquot(aliquotaIcms)) {
            throw new ValidationException("Alíquota ICMS inválida: " + aliquotaIcms);
        }
        
        // Valida se alíquota é compatível com UF
        if (uf != null && !uf.isEmpty()) {
            BigDecimal defaultAliquot = IcmsValidator.getDefaultAliquot(uf);
            // Apenas aviso, não erro - permitir variações
        }
    }
    
    /**
     * Valida CPF/CNPJ do cliente
     */
    public static void validateCustomerDocument(String document, String type) {
        if (document == null || document.isEmpty()) {
            throw new ValidationException(type + " é obrigatório");
        }
        
        if ("CPF".equalsIgnoreCase(type)) {
            if (!DocumentValidator.isValidCpf(document)) {
                throw new ValidationException("CPF inválido: " + document);
            }
        } else if ("CNPJ".equalsIgnoreCase(type)) {
            if (!DocumentValidator.isValidCnpj(document)) {
                throw new ValidationException("CNPJ inválido: " + document);
            }
        } else {
            throw new ValidationException("Tipo de documento inválido: " + type);
        }
    }
    
    /**
     * Valida valores monetários
     */
    public static void validateMonetaryValue(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " não pode ser nulo");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(fieldName + " não pode ser negativo");
        }
    }
    
    /**
     * Valida quantidade
     */
    public static void validateQuantity(BigDecimal quantity, String fieldName) {
        if (quantity == null) {
            throw new ValidationException(fieldName + " não pode ser nulo");
        }
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName + " deve ser maior que zero");
        }
    }
    
    /**
     * Valida email
     */
    public static void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return; // Email é opcional
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Email inválido: " + email);
        }
    }
    
    /**
     * Valida UF
     */
    public static void validateUf(String uf) {
        if (uf == null || uf.isEmpty()) {
            throw new ValidationException("UF é obrigatório");
        }
        if (!uf.matches("^[A-Z]{2}$")) {
            throw new ValidationException("UF inválido: " + uf + ". Use formato de 2 letras maiúsculas");
        }
    }
}
