package com.NFS_E.notaFiscalEletronica.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respostas de erro padronizadas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    
    private int status;
    private String message;
    private String errorCode;
    private String path;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    
    private List<FieldErrorDTO> fieldErrors;
    
    /**
     * DTO para erros em campos específicos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldErrorDTO {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
