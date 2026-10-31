package com.example.b_eletricar.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository repo;

    @InjectMocks
    private UsuarioDetailsService service;

    @Test
    void deveCarregarAdminComPerfilCorreto() {
        Usuario admin = new Usuario();
        admin.setEmail("admin@beletricar.com");
        admin.setSenha("hash-qualquer");
        admin.setRole(Roles.ADMIN);

        // o servico limpa os espacos e poe o e-mail em minusculas antes de buscar
        when(repo.findByEmail("admin@beletricar.com")).thenReturn(Optional.of(admin));

        UserDetails detalhes = service.loadUserByUsername("  ADMIN@Beletricar.com ");

        assertEquals("admin@beletricar.com", detalhes.getUsername());
        assertEquals("hash-qualquer", detalhes.getPassword());
        assertTrue(detalhes.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void deveFalharQuandoUsuarioNaoExiste() {
        when(repo.findByEmail("ninguem@exemplo.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
            () -> service.loadUserByUsername("ninguem@exemplo.com"));
    }

    @Test
    void deveFalharParaContaSoDoGoogleSemSenha() {
        Usuario conta = new Usuario();
        conta.setEmail("google@exemplo.com");
        conta.setSenha(null);
        conta.setRole(Roles.USER);

        when(repo.findByEmail("google@exemplo.com")).thenReturn(Optional.of(conta));

        assertThrows(UsernameNotFoundException.class,
            () -> service.loadUserByUsername("google@exemplo.com"));
    }
}