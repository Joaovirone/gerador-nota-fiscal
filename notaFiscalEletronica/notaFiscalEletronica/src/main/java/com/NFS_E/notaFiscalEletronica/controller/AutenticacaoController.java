package com.NFS_E.notaFiscalEletronica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NFS_E.notaFiscalEletronica.controller.dto.AuthDTO;
import com.NFS_E.notaFiscalEletronica.entity.Usuario;
import com.NFS_E.notaFiscalEletronica.infra.security.TokenService;
import com.NFS_E.notaFiscalEletronica.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/autenticacao")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login-usuario")
    public ResponseEntity efetuarLogin(@RequestBody @Valid AuthDTO dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
    
    @PostMapping("/cadastrar-usuario")
    public ResponseEntity cadastrar(@RequestBody @Valid AuthDTO dto) {
        
        usuarioService.cadastrarUsuario(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
        
    }
    
   
    private record DadosTokenJWT(String token) {}
}
