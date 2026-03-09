package com.NFS_E.notaFiscalEletronica.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDTO {
    
    
    @NotBlank(message="Login é obrigatório")
    @Email(message= "O login deve ser um email válido")
    private String login;
    
    @NotBlank(message="Senha é obrigatória")
    @Size(min=8, message= "A senha deve conter no mínimo 8 caracteres")
    private String senha;
}
