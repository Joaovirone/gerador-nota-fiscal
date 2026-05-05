package com.NFS_E.notaFiscalEletronica.infra.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.NFS_E.notaFiscalEletronica.controller.dto.ErrorResponseDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.ErrorResponseDTO.FieldErrorDTO;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler global para exceções da aplicação
 * Centraliza o tratamento de erros e padroniza as respostas
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Trata erros de validação em DTOs (anotações Jakarta Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, 
            WebRequest request) {
        
        log.warn("Erro de validação em request: {}", ex.getBindingResult());
        
        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            
            fieldErrors.add(FieldErrorDTO.builder()
                .field(fieldName)
                .message(message)
                .rejectedValue(rejectedValue)
                .build());
        });
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Erro de validação nos dados fornecidos")
            .errorCode("INVALID_REQUEST")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .fieldErrors(fieldErrors)
            .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata exceções de validação customizadas
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex,
            WebRequest request) {
        
        log.warn("Erro de validação: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .errorCode(ex.getErrorCode())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata exceções de NFe
     */
    @ExceptionHandler(NfeException.class)
    public ResponseEntity<ErrorResponseDTO> handleNfeException(
            NfeException ex,
            WebRequest request) {
        
        log.error("Erro de NFe: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .message(ex.getMessage())
            .errorCode(ex.getErrorCode())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }
    
    /**
     * Trata exceções de SEFAZ
     */
    @ExceptionHandler(SefazException.class)
    public ResponseEntity<ErrorResponseDTO> handleSefazException(
            SefazException ex,
            WebRequest request) {
        
        log.error("Erro de SEFAZ: {}", ex.getMessage(), ex);
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .message(ex.getMessage())
            .errorCode(ex.getErrorCode())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * Trata EntityNotFoundException (recurso não encontrado)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request) {
        
        log.warn("Entidade não encontrada: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .errorCode("ENTITY_NOT_FOUND")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Trata IllegalStateException (operação inválida para o estado atual)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalStateException(
            IllegalStateException ex,
            WebRequest request) {
        
        log.warn("Estado ilegal: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .errorCode("INVALID_STATE")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Trata IllegalArgumentException (argumento inválido)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        
        log.warn("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .errorCode("INVALID_ARGUMENT")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata erros de autenticação
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {
        
        log.warn("Erro de autenticação: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .message("Falha na autenticação. Verifique suas credenciais.")
            .errorCode("AUTHENTICATION_FAILED")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Trata erros de acesso negado
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {
        
        log.warn("Acesso negado: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .message("Acesso negado. Você não tem permissão para acessar este recurso.")
            .errorCode("ACCESS_DENIED")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    /**
     * Trata 404 - endpoint não encontrado
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            WebRequest request) {
        
        log.warn("Endpoint não encontrado: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message("Endpoint não encontrado: " + ex.getRequestURL())
            .errorCode("ENDPOINT_NOT_FOUND")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Trata todas as outras exceções não capturadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        log.error("Erro inesperado", ex);
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("Erro interno do servidor. Por favor, tente novamente mais tarde.")
            .errorCode("INTERNAL_SERVER_ERROR")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
