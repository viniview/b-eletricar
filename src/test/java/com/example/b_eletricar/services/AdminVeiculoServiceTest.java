package com.example.b_eletricar.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.b_eletricar.database.model.Fornecedor;
import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.repository.FornecedorRepository;
import com.example.b_eletricar.database.repository.VeiculoRepository;
import com.example.b_eletricar.dto.VeiculoForm;

@ExtendWith(MockitoExtension.class)
class AdminVeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private AdminVeiculoService service;

    private VeiculoForm form(String marca, String modelo, String carroceria, Double preco, Integer autonomiaKm,
            Double capacidadeBateriaKwh, Integer potenciaCv, Double tempoCargaHoras, Long fornecedorId) {
        VeiculoForm form = new VeiculoForm();
        form.setMarca(marca);
        form.setModelo(modelo);
        form.setCarroceria(carroceria);
        form.setPreco(preco);
        form.setAutonomiaKm(autonomiaKm);
        form.setCapacidadeBateriaKwh(capacidadeBateriaKwh);
        form.setPotenciaCv(potenciaCv);
        form.setTempoCargaHoras(tempoCargaHoras);
        form.setFornecedorId(fornecedorId);
        return form;
    }

    private Fornecedor fornecedor(Long id) {
        Fornecedor f = new Fornecedor();
        f.setId(id);
        f.setNome("Fornecedor " + id);
        return f;
    }

    @Test
    void criarAssociaFornecedorETrimCarroceriaVaziaViraNull() {
        VeiculoForm form = form("  Tesla  ", "  Model 3  ", "   ", 250000.0, 500, 75.0, 300, 8.5, 1L);
        Fornecedor fornecedor = fornecedor(1L);

        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Veiculo salvo = service.criar(form);

        assertThat(salvo.getMarca()).isEqualTo("Tesla");
        assertThat(salvo.getModelo()).isEqualTo("Model 3");
        assertThat(salvo.getCarroceria()).isNull();
        assertThat(salvo.getPreco()).isEqualTo(250000.0);
        assertThat(salvo.getAutonomiaKm()).isEqualTo(500);
        assertThat(salvo.getCapacidadeBateriaKwh()).isEqualTo(75.0);
        assertThat(salvo.getPotenciaCv()).isEqualTo(300);
        assertThat(salvo.getTempoCargaHoras()).isEqualTo(8.5);
        assertThat(salvo.getFornecedor()).isSameAs(fornecedor);
        verify(veiculoRepository).save(salvo);
    }

    @Test
    void criarComFornecedorInexistenteLancaENaoSalva() {
        VeiculoForm form = form("Tesla", "Model 3", "Sedan", 250000.0, 500, 75.0, 300, 8.5, 99L);

        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fornecedor inválido.");

        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    void atualizarAlteraCamposDoVeiculoExistente() {
        Veiculo existente = new Veiculo();
        existente.setId(1L);
        existente.setMarca("Antiga");
        existente.setModelo("Velho");
        existente.setCarroceria("Antiga");
        existente.setFornecedor(fornecedor(2L));

        Fornecedor novoFornecedor = fornecedor(1L);
        VeiculoForm form = form("  Nova  ", "  Novo  ", "", 199999.0, 600, 80.0, null, null, 1L);

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(novoFornecedor));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Veiculo atualizado = service.atualizar(1L, form);

        verify(veiculoRepository).save(existente);
        assertThat(atualizado).isSameAs(existente);
        assertThat(atualizado.getMarca()).isEqualTo("Nova");
        assertThat(atualizado.getModelo()).isEqualTo("Novo");
        assertThat(atualizado.getCarroceria()).isNull();
        assertThat(atualizado.getPreco()).isEqualTo(199999.0);
        assertThat(atualizado.getAutonomiaKm()).isEqualTo(600);
        assertThat(atualizado.getCapacidadeBateriaKwh()).isEqualTo(80.0);
        assertThat(atualizado.getPotenciaCv()).isNull();
        assertThat(atualizado.getTempoCargaHoras()).isNull();
        assertThat(atualizado.getFornecedor()).isSameAs(novoFornecedor);
    }

    @Test
    void buscarPorIdInexistenteLanca404() {
        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void excluirInexistenteLanca404ENaoChamaDelete() {
        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(veiculoRepository, never()).delete(any(Veiculo.class));
    }

    @Test
    void excluirExistenteChamaDelete() {
        Veiculo existente = new Veiculo();
        existente.setId(1L);

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(existente));

        service.excluir(1L);

        verify(veiculoRepository).delete(existente);
    }

}
