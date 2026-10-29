package com.example.b_eletricar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.b_eletricar.config.SecurityConfig;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.dto.CadastroForm;
import com.example.b_eletricar.security.CustomOidcUser;
import com.example.b_eletricar.security.LoginFailureHandler;
import com.example.b_eletricar.services.CadastroService;

// Carrega so a camada web (os controllers) e a configuracao de seguranca, sem bd
@WebMvcTest({HomeController.class, LoginController.class, CadastroController.class})
@Import(SecurityConfig.class)
// Valores falsos para o login com Google, para o teste nao depender do arquivo .env
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=teste",
    "spring.security.oauth2.client.registration.google.client-secret=teste"
})
class PaginasPublicasSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    // Pecas que o SecurityConfig e os controllers exigem aqui sao dubles (mocks)
    @MockitoBean
    private CustomOidcUser customOidcUserService;

    @MockitoBean
    private LoginFailureHandler loginFailureHandler;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private CadastroService cadastroService;

    //  Paginas publicas 

    @Test
    @WithAnonymousUser
    void anonimoAcessaPaginaInicial() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    @WithAnonymousUser
    void anonimoAcessaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithAnonymousUser
    void anonimoAcessaCadastro() throws Exception {
        mockMvc.perform(get("/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"));
    }

    // Paginas protegidas 

    @Test
    @WithAnonymousUser
    void anonimoNaoAcessaPerfilERedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/perfil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithAnonymousUser
    void anonimoNaoAcessaRotaQueExigeLogin() throws Exception {
        // tudo que nao esta na lista de rotas publicas exige login
        mockMvc.perform(get("/rota-protegida"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    //  Cadastro 

    @Test
    @WithAnonymousUser
    void cadastroValidoRedirecionaParaLogin() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "Maria")
                        .param("email", "maria@exemplo.com")
                        .param("senha", "senha1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?cadastrado"));
    }

    @Test
    @WithAnonymousUser
    void cadastroComEmailRepetidoMostraErro() throws Exception {
        when(cadastroService.cadastrar(any(CadastroForm.class)))
                .thenThrow(new IllegalArgumentException("E-mail já cadastrado."));

        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "Maria")
                        .param("email", "maria@exemplo.com")
                        .param("senha", "senha1234"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @WithAnonymousUser
    void cadastroComSenhaCurtaMostraErroDeValidacao() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "Maria")
                        .param("email", "maria@exemplo.com")
                        .param("senha", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeHasFieldErrors("form", "senha"));
    }

    @Test
    @WithAnonymousUser
    void cadastroSemTokenCsrfERecusado() throws Exception {
        // o formulario protegido contra pedidos forjados, sem o token, o Spring responde 403
        mockMvc.perform(post("/cadastro")
                        .param("nome", "Maria")
                        .param("email", "maria@exemplo.com")
                        .param("senha", "senha1234"))
                .andExpect(status().isForbidden());
    }
}