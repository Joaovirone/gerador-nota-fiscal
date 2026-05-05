package com.NFS_E.notaFiscalEletronica.infra.exception;

/**
 * Exceção para erros de validação
 */
public class ValidationException extends NfeException {
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
    }
}
