package com.example.b_eletricar.controller.admin;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.b_eletricar.config.SecurityConfig;
import com.example.b_eletricar.database.model.Comentario;
import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.security.CustomOidcUser;
import com.example.b_eletricar.security.LoginFailureHandler;
import com.example.b_eletricar.services.AdminVeiculoService;
import com.example.b_eletricar.services.ComentarioAdminService;

@WebMvcTest(AdminComentarioController.class)
@Import(SecurityConfig.class)
class AdminComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComentarioAdminService comentarioAdminService;

    @MockitoBean
    private AdminVeiculoService adminVeiculoService;

    @MockitoBean
    private CustomOidcUser customOidcUserService;

    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Comentario comentario(Long id) {
        Usuario autor = new Usuario();
        autor.setId(1L);
        autor.setNome("Administrador");
        autor.setEmail("admin@example.com");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setMarca("Tesla");
        veiculo.setModelo("Model 3");

        Comentario c = new Comentario();
        c.setId(id);
        c.setTexto("Comentário " + id);
        c.setAutor(autor);
        c.setVeiculo(veiculo);
        c.setDataCriacao(LocalDateTime.of(2026, 10, 28, 12, 0));
        return c;
    }

    @Test
    @WithAnonymousUser
    void anonimoRedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/admin/comentarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioComumRecebe403() throws Exception {
        mockMvc.perform(get("/admin/comentarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRecebeListaComPaginaEVeiculos() throws Exception {
        when(comentarioAdminService.listar(null, 0)).thenReturn(Page.empty());
        when(adminVeiculoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/admin/comentarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/comentarios/lista"))
                .andExpect(model().attributeExists("pagina"))
                .andExpect(model().attributeExists("veiculos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getComFiltroEPaginaChamaServiceEPropagaParametros() throws Exception {
        Page<Comentario> pagina = new PageImpl<>(List.of(comentario(1L)), PageRequest.of(2, 20), 21);

        when(comentarioAdminService.listar(5L, 2)).thenReturn(pagina);
        when(adminVeiculoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/admin/comentarios")
                        .param("veiculoId", "5")
                        .param("pagina", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/comentarios/lista"))
                .andExpect(model().attribute("pagina", pagina))
                .andExpect(model().attributeExists("veiculos"))
                .andExpect(model().attribute("veiculoId", 5L));

        verify(comentarioAdminService).listar(5L, 2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPaginaVaziaRedirecionaParaAnteriorPreservandoVeiculoId() throws Exception {
        when(comentarioAdminService.listar(5L, 3)).thenReturn(Page.empty());

        mockMvc.perform(get("/admin/comentarios")
                        .param("veiculoId", "5")
                        .param("pagina", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comentarios?pagina=2&veiculoId=5"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postExcluirComCsrfChamaExcluirEPreservaParametros() throws Exception {
        mockMvc.perform(post("/admin/comentarios/3/excluir")
                        .param("veiculoId", "5")
                        .param("pagina", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comentarios?veiculoId=5&pagina=3"))
                .andExpect(flash().attribute("sucesso", "Comentário excluído."));

        verify(comentarioAdminService).excluir(3L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSemCsrfRecebe403() throws Exception {
        mockMvc.perform(post("/admin/comentarios/3/excluir")
                        .param("veiculoId", "5")
                        .param("pagina", "3"))
                .andExpect(status().isForbidden());

        verify(comentarioAdminService, never()).excluir(anyLong());
    }

}
