package com.NFS_E.notaFiscalEletronica.infra.exception;

/**
 * Exceção base para erros relacionados a NFe
 */
public class NfeException extends RuntimeException {
    
    private final String errorCode;
    
    public NfeException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public NfeException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
