package com.NFS_E.notaFiscalEletronica.infra.web.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.NFS_E.notaFiscalEletronica.entity.AuditLog;
import com.NFS_E.notaFiscalEletronica.entity.Usuario;
import com.NFS_E.notaFiscalEletronica.repository.AuditLogRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço centralizado para auditoria de operações
 */
@Slf4j
@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    /**
     * Registra uma operação bem-sucedida
     */
    public void auditarOperacao(String acao, String entidade, UUID entidadeId, 
                               String descricao, Authentication authentication) {
        try {
            Usuario usuario = null;
            String nomeUsuario = "ANONIMO";
            UUID usuarioId = UUID.randomUUID();
            
            if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
                usuario = (Usuario) authentication.getPrincipal();
                nomeUsuario = usuario.getUsername();
                usuarioId = usuario.getId();
            }
            
            String ipOrigem = getClientIpAddress();
            String userAgent = getClientUserAgent();
            
            AuditLog auditLog = AuditLog.builder()
                .usuarioId(usuarioId)
                .nomeUsuario(nomeUsuario)
                .acao(acao)
                .entidade(entidade)
                .entidadeId(entidadeId)
                .descricao(descricao)
                .ipOrigem(ipOrigem)
                .userAgent(userAgent)
                .status("SUCCESS")
                .build();
            
            auditLogRepository.save(auditLog);
            
            log.info("Auditoria: {} - {} - {} - {}", nomeUsuario, acao, entidade, descricao);
        } catch (Exception e) {
            log.error("Erro ao registrar auditoria", e);
        }
    }
    
    /**
     * Registra uma operação com erro
     */
    public void auditarOperacaoComErro(String acao, String entidade, UUID entidadeId,
                                       String descricao, String erro, 
                                       Authentication authentication) {
        try {
            Usuario usuario = null;
            String nomeUsuario = "ANONIMO";
            UUID usuarioId = UUID.randomUUID();
            
            if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
                usuario = (Usuario) authentication.getPrincipal();
                nomeUsuario = usuario.getUsername();
                usuarioId = usuario.getId();
            }
            
            String ipOrigem = getClientIpAddress();
            String userAgent = getClientUserAgent();
            
            AuditLog auditLog = AuditLog.builder()
                .usuarioId(usuarioId)
                .nomeUsuario(nomeUsuario)
                .acao(acao)
                .entidade(entidade)
                .entidadeId(entidadeId)
                .descricao(descricao)
                .ipOrigem(ipOrigem)
                .userAgent(userAgent)
                .status("FAILURE")
                .erro(erro)
                .build();
            
            auditLogRepository.save(auditLog);
            
            log.warn("Auditoria (ERRO): {} - {} - {} - {} - {}", nomeUsuario, acao, entidade, descricao, erro);
        } catch (Exception e) {
            log.error("Erro ao registrar auditoria de erro", e);
        }
    }
    
    /**
     * Obtém o IP do cliente
     */
    private String getClientIpAddress() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes()).getRequest();
            
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                clientIp = request.getHeader("Proxy-Client-IP");
            }
            if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                clientIp = request.getHeader("WL-Proxy-Client-IP");
            }
            if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                clientIp = request.getRemoteAddr();
            }
            
            // Remove port se houver múltiplos IPs
            if (clientIp != null && clientIp.contains(",")) {
                clientIp = clientIp.split(",")[0].trim();
            }
            
            return clientIp;
        } catch (Exception e) {
            return "DESCONHECIDO";
        }
    }
    
    /**
     * Obtém o User Agent
     */
    private String getClientUserAgent() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes()).getRequest();
            return request.getHeader("User-Agent");
        } catch (Exception e) {
            return "DESCONHECIDO";
        }
    }
}
