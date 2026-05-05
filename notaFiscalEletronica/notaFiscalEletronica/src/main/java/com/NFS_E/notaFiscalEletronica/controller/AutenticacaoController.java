package com.NFS_E.notaFiscalEletronica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NFS_E.notaFiscalEletronica.controller.dto.AuthDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.RefreshTokenRequestDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.TokenResponseDTO;
import com.NFS_E.notaFiscalEletronica.entity.Usuario;
import com.NFS_E.notaFiscalEletronica.infra.security.TokenService;
import com.NFS_E.notaFiscalEletronica.service.UsuarioService;

import jakarta.validation.Valid;

/**
 * Controller para autenticação e gerenciamento de tokens
 */
@RestController
@RequestMapping("/api/v1/autenticacao")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenService tokenService;

    /**
     * Endpoint para login do usuário
     */
    @PostMapping("/login-usuario")
    public ResponseEntity<TokenResponseDTO> efetuarLogin(@RequestBody @Valid AuthDTO dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
        var authentication = manager.authenticate(authenticationToken);

        var usuario = (Usuario) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuario);
        var refreshToken = tokenService.gerarRefreshToken(usuario);

        TokenResponseDTO response = TokenResponseDTO.builder()
            .accessToken(tokenJWT)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L) // 1 hora
            .build();

        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para cadastro de novo usuário
     */
    @PostMapping("/cadastrar-usuario")
    public ResponseEntity<Object> cadastrar(@RequestBody @Valid AuthDTO dto) {
        usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso");
    }
    
    /**
     * Endpoint para renovar o access token usando refresh token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDTO> refreshToken(
        @RequestBody @Valid RefreshTokenRequestDTO request) {
        
        try {
            // Valida o refresh token e pega o login do usuário
            String usuarioLogin = tokenService.validarRefreshToken(request.getRefreshToken());
            
            // Busca o usuário
            Usuario usuario = usuarioService.findByLogin(usuarioLogin);
            
            // Gera novo access token
            String newAccessToken = tokenService.gerarToken(usuario);
            
            TokenResponseDTO response = TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1 hora
                .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(TokenResponseDTO.builder()
                    .accessToken(null)
                    .tokenType("Bearer")
                    .build());
        }
    }
    
    /**
     * Endpoint para logout (revoga refresh token)
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(
        @RequestBody @Valid RefreshTokenRequestDTO request) {
        
        try {
            tokenService.revogarRefreshToken(request.getRefreshToken());
            return ResponseEntity.ok("Logout realizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao fazer logout: " + e.getMessage());
        }
    }
}
