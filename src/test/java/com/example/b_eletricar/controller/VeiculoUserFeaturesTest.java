package com.example.b_eletricar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.b_eletricar.config.SecurityConfig;
import com.example.b_eletricar.database.model.Comentario;
import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.dto.AvaliacaoForm;
import com.example.b_eletricar.dto.VeiculoOpcao;
import com.example.b_eletricar.security.CustomOidcUser;
import com.example.b_eletricar.security.LoginFailureHandler;
import com.example.b_eletricar.services.AvaliacaoService;
import com.example.b_eletricar.services.Comparar;
import com.example.b_eletricar.services.VeiculoService;

@WebMvcTest({HomeController.class, VeiculoController.class, AvaliacaoController.class, ComparadorController.class})
@Import(SecurityConfig.class)
class VeiculoUserFeaturesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VeiculoService veiculoService;
    @MockitoBean
    private AvaliacaoService avaliacaoService;
    @MockitoBean
    private Comparar comparador;
    @MockitoBean
    private UsuarioRepository usuarioRepository;
    @MockitoBean
    private CustomOidcUser customOidcUserService;
    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @Test
    @WithAnonymousUser
    void homeEscondePesquisaParaVisitante() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Entre na sua conta")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Busca por filtros"))));
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "USER")
    void usuarioAutenticadoVeFiltrosNaHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Busca por filtros")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Buscar veículo, marca ou modelo")));
    }

    @Test
    @WithAnonymousUser
    void visitantePodeVerDetalhesEAvaliacoesDoVeiculo() throws Exception {
        Veiculo carro = veiculo(1L, "BYD", "Dolphin");
        Usuario autor = new Usuario();
        autor.setNome("Ana");
        Comentario comentario = new Comentario();
        comentario.setAutor(autor);
        comentario.setVeiculo(carro);
        comentario.setNota(5);
        comentario.setTexto("Muito confortável");
        comentario.setDataCriacao(LocalDateTime.of(2026, 10, 30, 12, 0));
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(carro));
        when(avaliacaoService.listarPorVeiculo(1L)).thenReturn(List.of(comentario));
        when(avaliacaoService.media(List.of(comentario))).thenReturn(5.0);

        mockMvc.perform(get("/veiculos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalhes-veiculo"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Dolphin")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Muito confortável")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("30/10/2026")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Entre na sua conta")));
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "USER")
    void usuarioAutenticadoVeFormularioParaAvaliar() throws Exception {
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(veiculo(1L, "BYD", "Dolphin")));
        when(avaliacaoService.listarPorVeiculo(1L)).thenReturn(List.of());
        when(avaliacaoService.media(List.of())).thenReturn(null);

        mockMvc.perform(get("/veiculos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Publicar avaliação")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"nota\"")));
    }

    @Test
    @WithAnonymousUser
    void visitanteNaoPodeUsarFiltrosNemAbrirComparador() throws Exception {
        mockMvc.perform(get("/veiculos/listagem").param("marca", "BYD"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/comparar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/api/comparador"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "USER")
    void buscaDoSeletorConsultaOpcoesNoServicoDeVeiculos() throws Exception {
        when(veiculoService.buscarParaComparacao("BYD", "Dolphin", null, "GS"))
                .thenReturn(List.of(veiculo(2L, "BYD", "Dolphin GS")));

        mockMvc.perform(get("/api/comparador")
                        .param("marca", "BYD")
                        .param("modelo", "Dolphin")
                        .param("busca", "GS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].marca").value("BYD"))
                .andExpect(jsonPath("$[0].modelo").value("Dolphin GS"));

        verify(veiculoService).buscarParaComparacao("BYD", "Dolphin", null, "GS");
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "USER")
    void usuarioSelecionaCarrosDoBancoERecebeComparacao() throws Exception {
        Veiculo primeiro = veiculo(1L, "BYD", "Dolphin");
        Veiculo segundo = veiculo(2L, "Renault", "Kwid");
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(primeiro));
        when(veiculoService.buscarVeiculoPorId(2L)).thenReturn(Optional.of(segundo));
        when(comparador.comparar(List.of(primeiro, segundo))).thenReturn(List.of());

        mockMvc.perform(get("/comparar").param("carro1", "1").param("carro2", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("comparar"))
                .andExpect(model().attribute("podeComparar", true))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Dolphin")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Kwid")));
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "USER")
    void comparacaoExibeAvaliacoesEDebaixoDoCard() throws Exception {
        Veiculo primeiro = veiculo(1L, "BYD", "Dolphin");
        Veiculo segundo = veiculo(2L, "Renault", "Kwid");
        Usuario autor = new Usuario();
        autor.setNome("Ana");
        Comentario comentario = new Comentario();
        comentario.setAutor(autor);
        comentario.setVeiculo(primeiro);
        comentario.setNota(5);
        comentario.setTexto("Bateria excelente para o dia a dia");
        comentario.setDataCriacao(LocalDateTime.of(2026, 10, 30, 12, 0));
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(primeiro));
        when(veiculoService.buscarVeiculoPorId(2L)).thenReturn(Optional.of(segundo));
        when(comparador.comparar(List.of(primeiro, segundo))).thenReturn(List.of());
        when(avaliacaoService.listarPorVeiculo(1L)).thenReturn(List.of(comentario));
        when(avaliacaoService.media(List.of(comentario))).thenReturn(5.0);

        mockMvc.perform(get("/comparar").param("carro1", "1").param("carro2", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Avaliações dos usuários")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bateria excelente para o dia a dia")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nota média")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Avaliar este veículo")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Este veículo ainda não recebeu avaliações.")));
    }

    @Test
    @WithAnonymousUser
    void visitanteNaoPodeEnviarAvaliacaoEPostSemCsrfRetorna403() throws Exception {
        mockMvc.perform(post("/veiculos/1/avaliacoes")
                        .with(csrf())
                        .param("nota", "5")
                        .param("texto", "Muito bom"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/veiculos/1/avaliacoes")
                        .param("nota", "5")
                        .param("texto", "Muito bom"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "USER")
    void usuarioPublicaAvaliacaoEComentario() throws Exception {
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(veiculo(1L, "BYD", "Dolphin")));
        when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(new Usuario()));

        mockMvc.perform(post("/veiculos/1/avaliacoes")
                        .with(csrf())
                        .param("nota", "5")
                        .param("texto", "Ótimo carro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/veiculos/1"));

        verify(avaliacaoService).avaliar(any(Long.class), org.mockito.ArgumentMatchers.eq("ana@example.com"),
                any(AvaliacaoForm.class));
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "USER")
    void avaliacaoComNotaInvalidaMostraErroSemSalvar() throws Exception {
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(veiculo(1L, "BYD", "Dolphin")));
        when(avaliacaoService.listarPorVeiculo(1L)).thenReturn(List.of());
        when(avaliacaoService.media(List.of())).thenReturn(null);

        mockMvc.perform(post("/veiculos/1/avaliacoes")
                        .with(csrf())
                        .param("nota", "6")
                        .param("texto", "Comentário"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalhes-veiculo"))
                .andExpect(model().attributeHasFieldErrors("avaliacaoForm", "nota"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Confira a nota")));

        verify(avaliacaoService, never()).avaliar(any(Long.class), any(String.class), any(AvaliacaoForm.class));
    }

    @Test
    @WithMockUser(username = "ana@example.com", roles = "USER")
    void detalheDeVeiculoInexistenteRetorna404() throws Exception {
        when(veiculoService.buscarVeiculoPorId(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/veiculos/404"))
                .andExpect(status().isNotFound());
    }

    private Veiculo veiculo(Long id, String marca, String modelo) {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(id);
        veiculo.setMarca(marca);
        veiculo.setModelo(modelo);
        return veiculo;
    }
}
