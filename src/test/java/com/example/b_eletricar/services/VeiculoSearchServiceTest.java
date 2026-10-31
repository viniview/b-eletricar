package com.example.b_eletricar.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.repository.VeiculoRepository;

@ExtendWith(MockitoExtension.class)
class VeiculoSearchServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;
    @InjectMocks
    private VeiculoService veiculoService;

    @Test
    void filtrosDoSeletorSaoAplicadosEmConsultaJpaOrdenada() {
        Veiculo carro = new Veiculo();
        carro.setId(3L);
        carro.setMarca("BYD");
        carro.setModelo("Dolphin GS");
        when(veiculoRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(carro));

        List<Veiculo> encontrados = veiculoService.buscarParaComparacao(
                "BYD", "Dolphin GS", "Hatch", "GS");

        assertThat(encontrados).containsExactly(carro);
        verify(veiculoRepository).findAll(any(Specification.class), any(Sort.class));
    }
}
