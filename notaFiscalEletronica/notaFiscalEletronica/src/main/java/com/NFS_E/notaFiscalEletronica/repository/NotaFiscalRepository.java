package com.NFS_E.notaFiscalEletronica.repository;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

public interface NotaFiscalRepository extends JpaRepository <NotaFiscal, UUID>{
    
    @Query("SELECT max(n.numero) FROM NotaFiscal n" )
    Long findMaxNumero();
}
