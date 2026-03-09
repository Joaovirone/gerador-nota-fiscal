package com.NFS_E.notaFiscalEletronica.controller.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.NFS_E.notaFiscalEletronica.controller.dto.ItemNotaFiscalResponseDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalRequestDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalResponseDTO;
import com.NFS_E.notaFiscalEletronica.entity.ItemNotaFiscal;
import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotaFiscalMapper {

    private final ModelMapper modelMapper;
    
    public NotaFiscal toEntity(NotaFiscalRequestDTO dto){

        return modelMapper.map(dto, NotaFiscal.class);
    }

    public NotaFiscalResponseDTO toResponse(NotaFiscal entity){

        return modelMapper.map(entity, NotaFiscalResponseDTO.class);

    }
}   
