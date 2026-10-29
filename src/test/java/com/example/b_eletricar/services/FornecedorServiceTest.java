package com.example.b_eletricar.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.example.b_eletricar.database.repository.FornecedorRepository;
import com.example.b_eletricar.database.repository.VeiculoRepository;
import com.example.b_eletricar.dto.FornecedorForm;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {

    @Mock
    private FornecedorRepository repo;

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private FornecedorService service;

    private FornecedorForm form(String nome, String emailContato, String site) {
        FornecedorForm form = new FornecedorForm();
        form.setNome(nome);
        form.setEmailContato(emailContato);
        form.setSite(site);
        return form;
    }

    private Fornecedor fornecedorExistente(Long id, String nome) {
        Fornecedor f = new Fornecedor();
        f.setId(id);
        f.setNome(nome);
        return f;
    }

    @Test
    void criarComSucesso_aplicaTrimEVaziosViramNull() {
        FornecedorForm form = form("  Tesla  ", "   ", "");

        when(repo.existsByNomeIgnoreCase("Tesla")).thenReturn(false);
        when(repo.save(any(Fornecedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Fornecedor salvo = service.criar(form);

        verify(repo).existsByNomeIgnoreCase("Tesla");
        assertThat(salvo.getNome()).isEqualTo("Tesla");
        assertThat(salvo.getEmailContato()).isNull();
        assertThat(salvo.getSite()).isNull();
    }

    @Test
    void criarComNomeDuplicadoLancaENaoSalva() {
        FornecedorForm form = form("Tesla", null, null);

        when(repo.existsByNomeIgnoreCase("Tesla")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Já existe um fornecedor com esse nome.");

        verify(repo, never()).save(any(Fornecedor.class));
    }

    @Test
    void atualizarIgnoraProprioRegistroNaChecagemDeDuplicidade() {
        Fornecedor existente = fornecedorExistente(1L, "Tesla");
        FornecedorForm form = form("  Tesla  ", "novo@tesla.com", "https://tesla.com");

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.existsByNomeIgnoreCaseAndIdNot("Tesla", 1L)).thenReturn(false);
        when(repo.save(any(Fornecedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Fornecedor atualizado = service.atualizar(1L, form);

        verify(repo).existsByNomeIgnoreCaseAndIdNot("Tesla", 1L);
        verify(repo, never()).existsByNomeIgnoreCase(anyString());
        verify(repo).save(existente);
        assertThat(atualizado.getNome()).isEqualTo("Tesla");
        assertThat(atualizado.getEmailContato()).isEqualTo("novo@tesla.com");
    }

    @Test
    void buscarPorIdInexistenteLanca404() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void excluirSemVeiculosChamaDelete() {
        Fornecedor existente = fornecedorExistente(1L, "Tesla");

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(veiculoRepository.existsByFornecedorId(1L)).thenReturn(false);

        service.excluir(1L);

        verify(repo).delete(existente);
    }

    @Test
    void excluirComVeiculosLancaENaoDeleta() {
        Fornecedor existente = fornecedorExistente(1L, "Tesla");

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(veiculoRepository.existsByFornecedorId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não é possível excluir: há veículos vinculados a este fornecedor.");

        verify(repo, never()).delete(any(Fornecedor.class));
    }

}
