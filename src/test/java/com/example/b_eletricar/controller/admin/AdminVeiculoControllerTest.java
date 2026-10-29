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
import com.example.b_eletricar.dto.VeiculoForm;
import com.example.b_eletricar.security.CustomOidcUser;
import com.example.b_eletricar.security.LoginFailureHandler;
import com.example.b_eletricar.services.AdminVeiculoService;
import com.example.b_eletricar.services.FornecedorService;

@WebMvcTest(AdminVeiculoController.class)
@Import(SecurityConfig.class)
class AdminVeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminVeiculoService veiculoService;

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
        mockMvc.perform(get("/admin/veiculos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioComumRecebe403() throws Exception {
        mockMvc.perform(get("/admin/veiculos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRecebeLista() throws Exception {
        when(veiculoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/admin/veiculos"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/veiculos/lista"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getNovoMostraFormComAtributos() throws Exception {
        when(fornecedorService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/admin/veiculos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/veiculos/form"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("modo", "novo"))
                .andExpect(model().attributeExists("fornecedores"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postCriacaoComCsrfRedireciona() throws Exception {
        mockMvc.perform(post("/admin/veiculos")
                        .param("marca", "Tesla")
                        .param("modelo", "Model 3")
                        .param("preco", "150000")
                        .param("autonomiaKm", "400")
                        .param("capacidadeBateriaKwh", "75")
                        .param("fornecedorId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/veiculos"))
                .andExpect(flash().attribute("sucesso", "Veículo cadastrado."));

        verify(veiculoService).criar(any(VeiculoForm.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postDadosInvalidosVoltaParaForm() throws Exception {
        when(fornecedorService.listar()).thenReturn(List.of());

        mockMvc.perform(post("/admin/veiculos")
                        .param("marca", "")
                        .param("preco", "-10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/veiculos/form"))
                .andExpect(model().attribute("modo", "novo"))
                .andExpect(model().attributeExists("fornecedores"));

        verify(veiculoService, never()).criar(any(VeiculoForm.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postEdicaoInvalidaMantemModoEditar() throws Exception {
        when(fornecedorService.listar()).thenReturn(List.of());

        mockMvc.perform(post("/admin/veiculos/{id}", 1L)
                        .param("marca", "")
                        .param("preco", "-10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/veiculos/form"))
                .andExpect(model().attribute("modo", "editar"))
                .andExpect(model().attribute("veiculoId", 1L))
                .andExpect(model().attributeExists("fornecedores"));

        verify(veiculoService, never()).atualizar(any(Long.class), any(VeiculoForm.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSemCsrfRecebe403() throws Exception {
        mockMvc.perform(post("/admin/veiculos")
                        .param("marca", "Tesla")
                        .param("modelo", "Model 3")
                        .param("preco", "150000")
                        .param("autonomiaKm", "400")
                        .param("capacidadeBateriaKwh", "75")
                        .param("fornecedorId", "1"))
                .andExpect(status().isForbidden());

        verify(veiculoService, never()).criar(any(VeiculoForm.class));
    }

}
