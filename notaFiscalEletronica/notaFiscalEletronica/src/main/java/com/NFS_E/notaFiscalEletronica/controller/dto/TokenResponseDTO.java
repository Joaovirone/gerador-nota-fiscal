package com.NFS_E.notaFiscalEletronica.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login e refresh token
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn; // tempo de expiração em segundos
    
    public TokenResponseDTO(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = 3600L; // 1 hora
    }
}
