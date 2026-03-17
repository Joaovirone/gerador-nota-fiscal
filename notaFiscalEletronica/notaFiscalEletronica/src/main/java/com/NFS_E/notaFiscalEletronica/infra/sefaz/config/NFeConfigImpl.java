package com.NFS_E.notaFiscalEletronica.infra.sefaz.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFConfig;
import com.fincatto.documentofiscal.DFUnidadeFederativa;


@Component
public class NFeConfigImpl extends DFConfig {
    
    @Value("${sefaz.nfe.certificado.caminho}")
    private String certificadoCaminho;

    @Value("${sefaz.nfe.certificado.senha}")
    private String certificadoSenha;

    @Override
    public DFUnidadeFederativa getCUF() {
        
        return DFUnidadeFederativa.SE; 
    }
    
    @Override
    public DFAmbiente getAmbiente() {

        return DFAmbiente.HOMOLOGACAO;
    }

    @Override
    public KeyStore getCertificadoKeyStore() {

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream is = new FileInputStream(certificadoCaminho)) {
                keyStore.load(is, certificadoSenha.toCharArray());
            }
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar KeyStore do certificado: " + e.getMessage());
        }
    }

    @Override
    public String getCertificadoSenha() {
        return certificadoSenha;
    }

    @Override
    public String getCadeiaCertificadosSenha() {
        return "";
    }

    @Override
    public KeyStore getCadeiaCertificadosKeyStore() {
        return null;
    }
}