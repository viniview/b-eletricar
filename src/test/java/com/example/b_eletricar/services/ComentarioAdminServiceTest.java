package com.example.b_eletricar.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.b_eletricar.database.model.Comentario;
import com.example.b_eletricar.database.repository.ComentarioRepository;

@ExtendWith(MockitoExtension.class)
class ComentarioAdminServiceTest {

    @Mock
    private ComentarioRepository repo;

    @InjectMocks
    private ComentarioAdminService service;

    private Comentario comentario(Long id) {
        Comentario c = new Comentario();
        c.setId(id);
        c.setTexto("Comentário " + id);
        return c;
    }

    @Test
    void listarSemVeiculoIdUsaFindAll() {
        when(repo.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<Comentario> resultado = service.listar(null, 0);

        assertThat(resultado).isEmpty();
        verify(repo).findAll(any(Pageable.class));
        verify(repo, never()).findByVeiculoId(any(Long.class), any(Pageable.class));
    }

    @Test
    void listarComVeiculoIdUsaFindByVeiculoId() {
        when(repo.findByVeiculoId(eq(5L), any(Pageable.class))).thenReturn(Page.empty());

        Page<Comentario> resultado = service.listar(5L, 0);

        assertThat(resultado).isEmpty();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findByVeiculoId(eq(5L), pageableCaptor.capture());
        verify(repo, never()).findAll(any(Pageable.class));
    }

    @Test
    void listarUsaPaginaDe20EOrdenaPorDataCriacaoEDesempateId() {
        when(repo.findAll(any(Pageable.class))).thenReturn(Page.empty());

        service.listar(null, 0);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getSort().getOrderFor("dataCriacao")).isNotNull()
                .extracting(order -> order.getProperty() + "-" + order.getDirection())
                .isEqualTo("dataCriacao-DESC");
        assertThat(pageable.getSort().getOrderFor("id")).isNotNull()
                .extracting(order -> order.getProperty() + "-" + order.getDirection())
                .isEqualTo("id-DESC");
    }

    @Test
    void paginaNegativaViraZero() {
        when(repo.findAll(any(Pageable.class))).thenReturn(Page.empty());

        service.listar(null, -3);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
    }

    @Test
    void buscarPorIdInexistenteLanca404() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void excluirInexistenteLanca404ENaoChamaDelete() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(repo, never()).delete(any(Comentario.class));
    }

    @Test
    void excluirExistenteChamaDelete() {
        Comentario existente = comentario(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(existente));

        service.excluir(1L);

        verify(repo).delete(existente);
    }

}
