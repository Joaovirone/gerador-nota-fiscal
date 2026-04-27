package com.NFS_E.notaFiscalEletronica.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

public interface NotaFiscalRepository extends JpaRepository <NotaFiscal, UUID>, JpaSpecificationExecutor<NotaFiscal>{
    
    @Query("SELECT max(n.numero) FROM NotaFiscal n" )
    Long findMaxNumero();
}
