package com.NFS_E.notaFiscalEletronica.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

public interface NotaFiscalRepository extends JpaRepository <NotaFiscal, UUID>{
    
}
