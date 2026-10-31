package com.example.b_eletricar.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.example.b_eletricar.database.model.Comentario;
import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.repository.ComentarioRepository;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.database.repository.VeiculoRepository;
import com.example.b_eletricar.dto.AvaliacaoForm;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VeiculoRepository veiculoRepository;
    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Test
    void salvaNotaComentarioEAssociacoesDoUsuarioECarro() {
        Usuario autor = new Usuario();
        autor.setEmail("ana@example.com");
        Veiculo veiculo = new Veiculo();
        veiculo.setId(7L);
        when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(autor));
        when(veiculoRepository.findById(7L)).thenReturn(Optional.of(veiculo));
        when(comentarioRepository.save(any(Comentario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        AvaliacaoForm form = new AvaliacaoForm();
        form.setNota(5);
        form.setTexto(" Ótimo carro! ");

        Comentario resultado = avaliacaoService.avaliar(7L, "ana@example.com", form);

        assertThat(resultado.getNota()).isEqualTo(5);
        assertThat(resultado.getTexto()).isEqualTo("Ótimo carro!");
        assertThat(resultado.getAutor()).isSameAs(autor);
        assertThat(resultado.getVeiculo()).isSameAs(veiculo);
        verify(comentarioRepository).save(resultado);
    }

    @Test
    void notaForaDoIntervaloGeraErroSemConsultarOuSalvar() {
        AvaliacaoForm form = new AvaliacaoForm();
        form.setNota(6);
        form.setTexto("Comentário válido");

        assertThatThrownBy(() -> avaliacaoService.avaliar(7L, "ana@example.com", form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nota de 1 a 5");
    }

    @Test
    void veiculoInexistenteRetornaErroNotFound() {
        Usuario autor = new Usuario();
        when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(autor));
        when(veiculoRepository.findById(404L)).thenReturn(Optional.empty());
        AvaliacaoForm form = new AvaliacaoForm();
        form.setNota(4);
        form.setTexto("Comentário válido");

        assertThatThrownBy(() -> avaliacaoService.avaliar(404L, "ana@example.com", form))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
