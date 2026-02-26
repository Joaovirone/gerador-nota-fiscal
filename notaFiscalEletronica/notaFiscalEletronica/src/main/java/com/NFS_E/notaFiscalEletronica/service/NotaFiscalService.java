package com.NFS_E.notaFiscalEletronica.service;

import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.repository.NotaFiscalRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotaFiscalService {
    
    private final NotaFiscalRepository repository;

    @Transactional
    public NotaFiscal emitir(NotaFiscal nota){

        nota.calcularTotal();

        return repository.save(nota);
    }
}
