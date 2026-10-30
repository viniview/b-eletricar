package com.example.b_eletricar.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    private static final String EMAIL = "admin@example.com";
    private static final String SENHA = "12345678admin";

    @Mock
    private UsuarioRepository repo;

    @Mock
    private PasswordEncoder encoder;

    private AdminSeeder novoSeeder(String email, String senha) {
        AdminSeeder seeder = new AdminSeeder(repo, encoder);
        ReflectionTestUtils.setField(seeder, "adminEmail", email);
        ReflectionTestUtils.setField(seeder, "adminPassword", senha);
        return seeder;
    }

    @Test
    void configuracaoVazia_naoSalva() {
        for (String email : new String[] { null, "", "   " }) {
            novoSeeder(email, SENHA).run();
        }
        for (String senha : new String[] { null, "", "   " }) {
            novoSeeder(EMAIL, senha).run();
        }

        verifyNoInteractions(repo, encoder);
    }

    @Test
    void senhaMenorQue8NaoSalva() {
        novoSeeder(EMAIL, "1234567").run();
        novoSeeder(EMAIL, "a".repeat(73)).run(); // guarda extra: senha com mais de 72 caracteres

        verifyNoInteractions(repo, encoder);
    }

    @Test
    void usuarioInexistente_criaAdminComSenhaCodificada() {
        when(repo.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(encoder.encode(SENHA)).thenReturn("$2a$10$hashfalso");

        novoSeeder("  Admin@Example.COM ", SENHA).run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(captor.capture());
        Usuario salvo = captor.getValue();

        assertThat(salvo.getEmail()).isEqualTo(EMAIL);
        assertThat(salvo.getRole()).isEqualTo(Roles.ADMIN);
        assertThat(salvo.getSenha()).isEqualTo("$2a$10$hashfalso");
        assertThat(salvo.getSenha()).isNotEqualTo(SENHA);
        assertThat(salvo.getNome()).isEqualTo("Administrador");
    }

    @Test
    void usuarioExistente_promoveASemAlterarSenha() {
        Usuario existente = new Usuario();
        existente.setEmail(EMAIL);
        existente.setRole(Roles.USER);
        existente.setSenha("$2a$10$existente");

        when(repo.findByEmail(EMAIL)).thenReturn(Optional.of(existente));

        novoSeeder(EMAIL, SENHA).run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(captor.capture());

        assertThat(captor.getValue()).isSameAs(existente);
        assertThat(existente.getRole()).isEqualTo(Roles.ADMIN);
        assertThat(existente.getSenha()).isEqualTo("$2a$10$existente");
        verify(encoder, never()).encode(anyString());
    }

    @Test
    void idempotencia_duasVezesNaoCriaDoisUsuarios() {
        AtomicReference<Usuario> store = new AtomicReference<>();
        when(repo.findByEmail(anyString())).thenAnswer(invocation -> Optional.ofNullable(store.get()));
        when(repo.save(any(Usuario.class))).thenAnswer(invocation -> {
            store.set(invocation.getArgument(0));
            return invocation.getArgument(0);
        });
        when(encoder.encode(SENHA)).thenReturn("$2a$10$hashfalso");

        AdminSeeder seeder = novoSeeder(EMAIL, SENHA);
        seeder.run();
        seeder.run();

        verify(repo, times(2)).findByEmail(EMAIL);
        verify(repo, times(1)).save(any(Usuario.class));
        verify(encoder, times(1)).encode(SENHA);

        assertThat(store.get()).isNotNull();
        assertThat(store.get().getEmail()).isEqualTo(EMAIL);
        assertThat(store.get().getRole()).isEqualTo(Roles.ADMIN);
        assertThat(store.get().getSenha()).isEqualTo("$2a$10$hashfalso");
    }

}
