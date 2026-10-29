package com.example.b_eletricar.services;

// Metodos de verificacao do JUnit
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Metodos do Mockito para configurar e verificar simulacoes
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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.dto.CadastroForm;

// Habilita a integracao entre JUnit e Mockito.
@ExtendWith(MockitoExtension.class)
class CadastroServiceTest {

    // Repositorio simulado
    @Mock
    private UsuarioRepository repo;

    // Codificador de senha simulado.
    @Mock
    private PasswordEncoder encoder;

    // Cria o CadastroService e injeta nele os objetos simulados acima.
    @InjectMocks
    private CadastroService service;

    private CadastroForm formulario(String nome, String email, String senha) {
        CadastroForm form = new CadastroForm();
        form.setNome(nome);
        form.setEmail(email);
        form.setSenha(senha);
        return form;
    }

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        // prepara os dados e o comportamento esperado
        CadastroForm form = formulario("  Maria  ", "  Maria@Exemplo.COM ", "senha1234");

        when(repo.findByEmail("maria@exemplo.com")).thenReturn(Optional.empty());
        when(encoder.encode("senha1234")).thenReturn("hash-falso");
        // o save devolve o proprio objeto que recebeu
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // executa o metodo que queremos testar
        Usuario resultado = service.cadastrar(form);

        // ASSERT verifica se o resultado esta correto
        assertEquals("Maria", resultado.getNome());               // sem espacos sobrando
        assertEquals("maria@exemplo.com", resultado.getEmail());  // em minusculas
        assertEquals("hash-falso", resultado.getSenha());         // senha criptografada
        assertEquals(Roles.USER, resultado.getRole());            // novo cadastro e usuario comum
        verify(repo).save(any(Usuario.class));
    }

    @Test
    void naoDeveCadastrarEmailJaCadastrado() {
        Usuario existente = new Usuario();
        existente.setEmail("maria@exemplo.com");
        existente.setSenha("hash-qualquer");

        when(repo.findByEmail("maria@exemplo.com")).thenReturn(Optional.of(existente));

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
            () -> service.cadastrar(formulario("Maria", "maria@exemplo.com", "senha1234")));

        assertTrue(erro.getMessage().contains("já cadastrado"));
        // nada pode ser salvo quando o e-mail ja existe
        verify(repo, never()).save(any(Usuario.class));
    }

    @Test
    void naoDeveCadastrarEmailQueJaTemContaGoogle() {
        // conta criada so pelo Google nao tem senha local
        Usuario conta = new Usuario();
        conta.setEmail("maria@exemplo.com");
        conta.setSenha(null);

        when(repo.findByEmail("maria@exemplo.com")).thenReturn(Optional.of(conta));

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
            () -> service.cadastrar(formulario("Maria", "maria@exemplo.com", "senha1234")));

        assertTrue(erro.getMessage().contains("Google"));
        verify(repo, never()).save(any(Usuario.class));
    }
}