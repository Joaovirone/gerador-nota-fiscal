package com.NFS_E.notaFiscalEletronica;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.NFS_E.notaFiscalEletronica.controller.dto.ItemNotaFiscalRequestDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalRequestDTO;
import com.NFS_E.notaFiscalEletronica.controller.dto.NotaFiscalResponseDTO;
import com.NFS_E.notaFiscalEletronica.entity.NotaFiscal;
import com.NFS_E.notaFiscalEletronica.entity.enums.StatusNota;
import com.NFS_E.notaFiscalEletronica.repository.NotaFiscalRepository;
import com.NFS_E.notaFiscalEletronica.service.NotaFiscalService;

@SpringBootTest
@ActiveProfiles("test")
public class NotaFiscalServiceIntegrationTest {

    @Autowired
    private NotaFiscalService notaFiscalService;

    @Autowired
    private NotaFiscalRepository notaFiscalRepository;

    @Test
    void deveEmitirETransmitirNotaFiscal() {
        ItemNotaFiscalRequestDTO item = new ItemNotaFiscalRequestDTO(
                "Produto de Teste",
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(10),
                "21069010",
                "5102",
                "000",
                BigDecimal.valueOf(18)
        );

        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO("Cliente Teste", List.of(item));

        NotaFiscalResponseDTO resposta = notaFiscalService.emitir(request);

        assertThat(resposta).isNotNull();
        assertThat(resposta.getId()).isNotNull();
        assertThat(resposta.getStatus()).isEqualTo(StatusNota.PROCESSANDO.name());
        assertThat(resposta.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(20));

        UUID id = resposta.getId();
        NotaFiscal notaSalva = notaFiscalRepository.findById(id).orElseThrow();
        assertThat(notaSalva.getStatus()).isEqualTo(StatusNota.PROCESSANDO);

        NotaFiscalResponseDTO transmitida = notaFiscalService.transmitir(id);
        assertThat(transmitida.getStatus()).isEqualTo(StatusNota.AUTORIZADA.name());

        NotaFiscal notaAtualizada = notaFiscalRepository.findById(id).orElseThrow();
        assertThat(notaAtualizada.getStatus()).isEqualTo(StatusNota.AUTORIZADA);
        assertThat(notaAtualizada.getChaveAcesso()).isNotBlank();
        assertThat(notaAtualizada.getXmlAssinado()).contains("NFe");
    }
}
