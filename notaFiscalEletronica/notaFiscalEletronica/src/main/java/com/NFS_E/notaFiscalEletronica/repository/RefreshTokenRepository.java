package com.NFS_E.notaFiscalEletronica.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.NFS_E.notaFiscalEletronica.entity.RefreshToken;
import com.NFS_E.notaFiscalEletronica.entity.Usuario;

/**
 * Repositório para RefreshToken
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByUsuario(Usuario usuario);
    
    void deleteByUsuario(Usuario usuario);
    
    void deleteByToken(String token);
}
