package com.NFS_E.notaFiscalEletronica.domain.repository;

public interface NotaFiscalRepository {
    
    void salvar(NotaFiscal nota);

    Optional<NotaFiscal> buscarPorId(UUID id);
}
