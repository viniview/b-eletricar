package com.example.b_eletricar.controller.admin;

import static org.mockito.ArgumentMatchers.any;
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
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.dto.FornecedorForm;
import com.example.b_eletricar.security.CustomOidcUser;
import com.example.b_eletricar.security.LoginFailureHandler;
import com.example.b_eletricar.services.FornecedorService;

@WebMvcTest(AdminFornecedorController.class)
@Import(SecurityConfig.class)
class AdminFornecedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FornecedorService fornecedorService;

    @MockitoBean
    private CustomOidcUser customOidcUserService;

    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithAnonymousUser
    void anonimoRedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/admin/fornecedores"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioComumRecebe403() throws Exception {
        mockMvc.perform(get("/admin/fornecedores"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRecebeLista() throws Exception {
        when(fornecedorService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/admin/fornecedores"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fornecedores/lista"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCriacaoComCsrfRedireciona() throws Exception {
        mockMvc.perform(post("/admin/fornecedores")
                        .param("nome", "Tesla")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/fornecedores"))
                .andExpect(flash().attribute("sucesso", "Fornecedor cadastrado."));

        verify(fornecedorService).criar(any(FornecedorForm.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postNomeVazioVoltaParaForm() throws Exception {
        mockMvc.perform(post("/admin/fornecedores")
                        .param("nome", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fornecedores/form"))
                .andExpect(model().attribute("modo", "novo"));

        verify(fornecedorService, never()).criar(any(FornecedorForm.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSemCsrfRecebe403() throws Exception {
        mockMvc.perform(post("/admin/fornecedores")
                        .param("nome", "Tesla"))
                .andExpect(status().isForbidden());

        verify(fornecedorService, never()).criar(any(FornecedorForm.class));
    }

}
