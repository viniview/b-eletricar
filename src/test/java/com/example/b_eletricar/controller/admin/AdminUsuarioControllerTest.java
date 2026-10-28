package com.example.b_eletricar.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.b_eletricar.config.SecurityConfig;
import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.dto.UsuarioAdminForm;
import com.example.b_eletricar.security.CustomOidcUser;
import com.example.b_eletricar.security.LoginFailureHandler;
import com.example.b_eletricar.security.UsuarioDetails;
import com.example.b_eletricar.services.UsuarioAdminService;

@WebMvcTest(AdminUsuarioController.class)
@Import(SecurityConfig.class)
class AdminUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioAdminService usuarioAdminService;

    @MockitoBean
    private CustomOidcUser customOidcUserService;

    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuario(Long id, Roles role) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNome("Usuário " + id);
        u.setEmail("usuario" + id + "@exemplo.com");
        u.setSenha("123");
        u.setSubId(null);
        u.setRole(role);
        return u;
    }

    @Test
    @WithAnonymousUser
    void anonimoRedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioComumRecebe403() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRecebeListaComUsuariosEIdLogado() throws Exception {
        Usuario admin = usuario(1L, Roles.ADMIN);
        when(usuarioAdminService.listar()).thenReturn(List.of(admin));

        mockMvc.perform(get("/admin/usuarios").with(user(new UsuarioDetails(admin))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios/lista"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("idLogado", 1L));
    }

    @Test
    void getEditarComIdDeOutroUsuarioProprioFalse() throws Exception {
        Usuario admin = usuario(1L, Roles.ADMIN);
        Usuario alvo = usuario(2L, Roles.USER);
        UsuarioAdminForm form = new UsuarioAdminForm();

        when(usuarioAdminService.buscarPorId(2L)).thenReturn(alvo);
        when(usuarioAdminService.paraForm(alvo)).thenReturn(form);

        mockMvc.perform(get("/admin/usuarios/2/editar").with(user(new UsuarioDetails(admin))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios/form"))
                .andExpect(model().attribute("form", form))
                .andExpect(model().attribute("usuario", alvo))
                .andExpect(model().attribute("usuarioId", 2L))
                .andExpect(model().attribute("proprio", false));
    }

    @Test
    void getEditarComIdDoLogadoProprioTrue() throws Exception {
        Usuario admin = usuario(1L, Roles.ADMIN);
        UsuarioAdminForm form = new UsuarioAdminForm();

        when(usuarioAdminService.buscarPorId(1L)).thenReturn(admin);
        when(usuarioAdminService.paraForm(admin)).thenReturn(form);

        mockMvc.perform(get("/admin/usuarios/1/editar").with(user(new UsuarioDetails(admin))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios/form"))
                .andExpect(model().attribute("form", form))
                .andExpect(model().attribute("usuario", admin))
                .andExpect(model().attribute("usuarioId", 1L))
                .andExpect(model().attribute("proprio", true));
    }

    @Test
    void postValidoRedirecionaEChamaAtualizarComIdAtor() throws Exception {
        Usuario admin = usuario(1L, Roles.ADMIN);

        mockMvc.perform(post("/admin/usuarios/2")
                        .param("nome", "Maria")
                        .param("role", "ADMIN")
                        .with(csrf())
                        .with(user(new UsuarioDetails(admin))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/usuarios"))
                .andExpect(flash().attribute("sucesso", "Usuário atualizado."));

        verify(usuarioAdminService).atualizar(eq(2L), any(UsuarioAdminForm.class), eq(1L));
    }

    @Test
    void postNomeVazioVoltaParaFormSemChamarAtualizar() throws Exception {
        Usuario admin = usuario(1L, Roles.ADMIN);
        Usuario alvo = usuario(2L, Roles.USER);

        when(usuarioAdminService.buscarPorId(2L)).thenReturn(alvo);

        mockMvc.perform(post("/admin/usuarios/2")
                        .param("nome", "")
                        .param("role", "USER")
                        .with(csrf())
                        .with(user(new UsuarioDetails(admin))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios/form"))
                .andExpect(model().attributeExists("usuarioId"))
                .andExpect(model().attributeExists("usuario"));

        verify(usuarioAdminService, never()).atualizar(any(Long.class), any(UsuarioAdminForm.class), any(Long.class));
    }

    @Test
    void postServiceLancaIllegalArgumentVoltaParaFormComErro() throws Exception {
        Usuario admin = usuario(1L, Roles.ADMIN);
        Usuario alvo = usuario(2L, Roles.ADMIN);

        when(usuarioAdminService.buscarPorId(2L)).thenReturn(alvo);
        when(usuarioAdminService.atualizar(eq(2L), any(UsuarioAdminForm.class), eq(1L)))
                .thenThrow(new IllegalArgumentException("O sistema precisa de pelo menos um administrador."));

        mockMvc.perform(post("/admin/usuarios/2")
                        .param("nome", "Admin Rebaixado")
                        .param("role", "USER")
                        .with(csrf())
                        .with(user(new UsuarioDetails(admin))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios/form"))
                .andExpect(model().attribute("erro", "O sistema precisa de pelo menos um administrador."))
                .andExpect(model().attributeExists("usuarioId"))
                .andExpect(model().attributeExists("usuario"));

        verify(usuarioAdminService).atualizar(eq(2L), any(UsuarioAdminForm.class), eq(1L));
    }

    @Test
    void postExcluirServiceLancaIllegalStateRedirectComErro() throws Exception {
        Usuario admin = usuario(1L, Roles.ADMIN);

        doThrow(new IllegalStateException("Você não pode excluir a sua própria conta."))
                .when(usuarioAdminService).excluir(1L, 1L);

        mockMvc.perform(post("/admin/usuarios/1/excluir")
                        .with(csrf())
                        .with(user(new UsuarioDetails(admin))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/usuarios"))
                .andExpect(flash().attribute("erro", "Você não pode excluir a sua própria conta."));
    }

    @Test
    void postSemCsrfRecebe403() throws Exception {
        Usuario admin = usuario(1L, Roles.ADMIN);

        mockMvc.perform(post("/admin/usuarios/2")
                        .param("nome", "Maria")
                        .param("role", "ADMIN")
                        .with(user(new UsuarioDetails(admin))))
                .andExpect(status().isForbidden());

        verify(usuarioAdminService, never()).atualizar(any(Long.class), any(UsuarioAdminForm.class), any(Long.class));
    }

}
