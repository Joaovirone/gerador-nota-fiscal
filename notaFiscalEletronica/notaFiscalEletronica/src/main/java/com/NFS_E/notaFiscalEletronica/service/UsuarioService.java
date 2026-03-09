package com.NFS_E.notaFiscalEletronica.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.controller.dto.AuthDTO;
import com.NFS_E.notaFiscalEletronica.entity.Usuario;
import com.NFS_E.notaFiscalEletronica.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void cadastrarUsuario(AuthDTO dto){

        if(repository.findByLogin(dto.getLogin()) != null){
            throw new RuntimeException("Este login já está em uso");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(dto.getLogin());
        novoUsuario.setSenha(senhaCriptografada);

        repository.save(novoUsuario);

    }
}
