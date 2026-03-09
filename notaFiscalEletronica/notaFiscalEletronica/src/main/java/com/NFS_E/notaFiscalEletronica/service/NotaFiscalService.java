package com.NFS_E.notaFiscalEletronica.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalRequestDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalResponseDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.PageResponseDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.mapper.NotaFiscalMapper;
import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.entity.enums.StatusNota;
import com.NFS_E.notaFiscalEletronica.repository.NotaFiscalRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotaFiscalService {
    
    private final NotaFiscalRepository repository;
    private final NotaFiscalMapper mapper;


    @Transactional
    public NotaFiscalResponseDTO emitir( NotaFiscalRequestDTO dto){

        NotaFiscal nota = mapper.toEntity(dto);

        if(nota.getItens() != null ){
            nota.getItens().forEach(item -> item.setNotaFiscal(nota));
        }

        nota.setStatus(StatusNota.PROCESSANDO);
        nota.calcularTotal();

        NotaFiscal notaSalva = repository.save(nota);

        return mapper.toResponse(notaSalva);


    }

    @Transactional
    public NotaFiscalResponseDTO cancelar(UUID id){
        NotaFiscal nota = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Nota não encontrada."));

        if(!nota.getStatus().equals(StatusNota.CANCELADA)){
            throw new IllegalStateException("Esta nota já está cancelada.");
        }

        if(!nota.getStatus().equals(StatusNota.REJEITADA)){

            throw new IllegalStateException("Não é possível cancelar uma nota que já está rejeitada.");
        }

        nota.setStatus(StatusNota.CANCELADA);

        NotaFiscal notaCancelada = repository.save(nota);

        return mapper.toResponse(notaCancelada);
    }



    @Transactional
    public NotaFiscalResponseDTO autorizar(UUID id){

        NotaFiscal nota = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Nota não encontrada"));

        if(!nota.getStatus().equals(StatusNota.PROCESSANDO)){

            throw new IllegalStateException("Apenas notas em processamento podem ser autorizadas");
        }
        nota.setStatus(StatusNota.AUTORIZADA);

        NotaFiscal notaAutorizada = repository.save(nota);
        return mapper.toResponse(notaAutorizada);
    }

    public PageResponseDTO<NotaFiscalResponseDTO> listarTodas(Pageable paginacao){

        Page<NotaFiscal> page = repository.findAll(paginacao);

        Page<NotaFiscalResponseDTO> pageDto = page.map(mapper :: toResponse);

        return new PageResponseDTO<>(pageDto);
    }



    
}
