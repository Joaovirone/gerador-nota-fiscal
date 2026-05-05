package com.NFS_E.notaFiscalEletronica.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade para log de auditoria - rastreia todas as operações
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID usuarioId;
    
    @Column(nullable = false)
    private String nomeUsuario;
    
    @Column(nullable = false)
    private String acao; // CREATE, UPDATE, DELETE, VIEW, AUTHORIZE, TRANSMIT, CANCEL etc
    
    @Column(nullable = false)
    private String entidade; // NotaFiscal, ItemNotaFiscal, Usuario etc
    
    @Column
    private UUID entidadeId; // ID do objeto afetado
    
    @Column(columnDefinition = "TEXT")
    private String descricao; // Detalhes da operação
    
    @Column(columnDefinition = "TEXT")
    private String dadosAntes; // Estado anterior em JSON
    
    @Column(columnDefinition = "TEXT")
    private String dadosDepois; // Estado novo em JSON
    
    @Column(nullable = false)
    private String ipOrigem;
    
    @Column
    private String userAgent;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHora;
    
    @Column
    private String status; // SUCCESS, FAILURE
    
    @Column(columnDefinition = "TEXT")
    private String erro; // Mensagem de erro se algo falhar
}
