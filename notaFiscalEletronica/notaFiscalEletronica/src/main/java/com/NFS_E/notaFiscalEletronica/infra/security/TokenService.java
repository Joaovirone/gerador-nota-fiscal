package com.NFS_E.notaFiscalEletronica.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.RefreshToken;
import com.NFS_E.notaFiscalEletronica.entity.Usuario;
import com.NFS_E.notaFiscalEletronica.repository.RefreshTokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

/**
 * Serviço para gerenciar JWT e Refresh Tokens
 */
@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;
    
    @Value("${api.security.token.expiration:3600}") // 1 hora em segundos
    private Long tokenExpiration;
    
    @Value("${api.security.refresh-token.expiration:2592000}") // 30 dias em segundos
    private Long refreshTokenExpiration;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /**
     * Gera um JWT access token
     */
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer("NFS-e API")
                .withSubject(usuario.getLogin())
                .withClaim("usuarioId", usuario.getId().toString())
                .withExpiresAt(dataExpiracao(tokenExpiration))
                .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token jwt", exception);
        }
    }
    
    /**
     * Gera um refresh token
     */
    public String gerarRefreshToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            String token = JWT.create()
                .withIssuer("NFS-e API")
                .withSubject(usuario.getLogin())
                .withClaim("usuarioId", usuario.getId().toString())
                .withClaim("tipo", "refresh")
                .withExpiresAt(dataExpiracao(refreshTokenExpiration))
                .sign(algoritmo);
            
            // Salva o refresh token no banco
            RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .token(token)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                .revoked(false)
                .build();
            
            refreshTokenRepository.save(refreshToken);
            
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar refresh token", exception);
        }
    }
    
    /**
     * Valida e retorna usuário do refresh token
     */
    public String validarRefreshToken(String refreshToken) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            
            // Verifica o token JWT
            String subject = JWT.require(algoritmo)
                .withIssuer("NFS-e API")
                .build()
                .verify(refreshToken)
                .getSubject();
            
            // Verifica se existe no banco e não está revogado
            RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));
            
            if (token.getRevoked() || token.isExpired()) {
                throw new RuntimeException("Refresh token revogado ou expirado");
            }
            
            return subject;
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Refresh token inválido ou expirado", exception);
        }
    }
    
    /**
     * Revoga um refresh token
     */
    public void revogarRefreshToken(String refreshTokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshTokenStr)
            .orElse(null);
        
        if (token != null) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        }
    }
    
    /**
     * Data de expiração do token
     */
    private Instant dataExpiracao(Long segundos) {
        return LocalDateTime.now()
            .plusSeconds(segundos)
            .toInstant(ZoneOffset.of("-03:00"));
    }

    /**
     * Extrai o subject (login) do token JWT
     */
    public String getSubject(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                .withIssuer("NFS-e API")
                .build()
                .verify(tokenJWT)
                .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }
    
    /**
     * Extrai o ID do usuário do token JWT
     */
    public String getUsuarioId(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                .withIssuer("NFS-e API")
                .build()
                .verify(tokenJWT)
                .getClaim("usuarioId")
                .asString();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }
}
