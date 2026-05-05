package com.NFS_E.notaFiscalEletronica.infra.exception;

/**
 * Exceção para erros de integração com SEFAZ
 */
public class SefazException extends NfeException {
    
    public SefazException(String message) {
        super(message, "SEFAZ_ERROR");
    }
    
    public SefazException(String message, Throwable cause) {
        super(message, "SEFAZ_ERROR", cause);
    }
}
