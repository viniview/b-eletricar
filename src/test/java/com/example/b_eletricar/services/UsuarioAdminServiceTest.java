package com.example.b_eletricar.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.ComentarioRepository;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.dto.UsuarioAdminForm;

@ExtendWith(MockitoExtension.class)
class UsuarioAdminServiceTest {

    @Mock
    private UsuarioRepository repo;

    @Mock
    private ComentarioRepository comentarioRepository;

    @InjectMocks
    private UsuarioAdminService service;

    private UsuarioAdminForm form(String nome, Roles role) {
        UsuarioAdminForm form = new UsuarioAdminForm();
        form.setNome(nome);
        form.setRole(role);
        return form;
    }

    private Usuario usuario(Long id, String nome, String email, String senha, String subId, Roles role) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(senha);
        u.setSubId(subId);
        u.setRole(role);
        return u;
    }

    @Test
    void atualizarAplicaTrimMudaRoleENaoTocaEmailSenhaSubId() {
        Usuario alvo = usuario(2L, "Maria", "maria@antigo.com", "senha-antiga", "sub-antigo", Roles.USER);
        UsuarioAdminForm form = form("  Maria Santos  ", Roles.ADMIN);

        when(repo.findById(2L)).thenReturn(Optional.of(alvo));
        when(repo.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario atualizado = service.atualizar(2L, form, 1L);

        assertThat(atualizado.getNome()).isEqualTo("Maria Santos");
        assertThat(atualizado.getRole()).isEqualTo(Roles.ADMIN);
        assertThat(atualizado.getEmail()).isEqualTo("maria@antigo.com");
        assertThat(atualizado.getSenha()).isEqualTo("senha-antiga");
        assertThat(atualizado.getSubId()).isEqualTo("sub-antigo");
        verify(repo).save(alvo);
    }

    @Test
    void atualizarProprioUsuarioParaUserLancaENaoSalva() {
        Usuario proprio = usuario(1L, "Admin", "admin@x.com", "123", "sub-1", Roles.ADMIN);
        UsuarioAdminForm form = form("Admin", Roles.USER);

        when(repo.findById(1L)).thenReturn(Optional.of(proprio));

        assertThatThrownBy(() -> service.atualizar(1L, form, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Você não pode remover o seu próprio perfil de administrador.");

        verify(repo, never()).save(any(Usuario.class));
    }

    @Test
    void atualizarProprioUsuarioMantendoAdminFunciona() {
        Usuario proprio = usuario(1L, "Admin", "admin@x.com", "123", "sub-1", Roles.ADMIN);
        UsuarioAdminForm form = form("  Admin Chefe  ", Roles.ADMIN);

        when(repo.findById(1L)).thenReturn(Optional.of(proprio));
        when(repo.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario atualizado = service.atualizar(1L, form, 1L);

        assertThat(atualizado.getNome()).isEqualTo("Admin Chefe");
        assertThat(atualizado.getRole()).isEqualTo(Roles.ADMIN);
        verify(repo).save(proprio);
    }

    @Test
    void rebaixarUnicoAdminLancaExceptionENaoSalva() {
        Usuario alvo = usuario(2L, "Admin", "admin@x.com", "123", "sub-2", Roles.ADMIN);
        UsuarioAdminForm form = form("Admin", Roles.USER);

        when(repo.findById(2L)).thenReturn(Optional.of(alvo));
        when(repo.countByRole(Roles.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> service.atualizar(2L, form, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O sistema precisa de pelo menos um administrador.");

        verify(repo, never()).save(any(Usuario.class));
    }

    @Test
    void rebaixarAdminComDoisOuMaisAdminsFunciona() {
        Usuario alvo = usuario(2L, "Admin", "admin@x.com", "123", "sub-2", Roles.ADMIN);
        UsuarioAdminForm form = form("Admin Rebaixado", Roles.USER);

        when(repo.findById(2L)).thenReturn(Optional.of(alvo));
        when(repo.countByRole(Roles.ADMIN)).thenReturn(2L);
        when(repo.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario atualizado = service.atualizar(2L, form, 1L);

        assertThat(atualizado.getRole()).isEqualTo(Roles.USER);
        verify(repo).save(alvo);
    }

    @Test
    void buscarPorIdInexistenteLanca404() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void excluirASiMesmoLancaENaoDeleta() {
        Usuario proprio = usuario(1L, "Admin", "admin@x.com", "123", "sub-1", Roles.ADMIN);

        when(repo.findById(1L)).thenReturn(Optional.of(proprio));

        assertThatThrownBy(() -> service.excluir(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Você não pode excluir a sua própria conta.");

        verify(repo, never()).delete(any(Usuario.class));
    }

    @Test
    void excluirUnicoAdminLancaExceptionENaoDeleta() {
        Usuario alvo = usuario(2L, "Admin", "admin@x.com", "123", "sub-2", Roles.ADMIN);

        when(repo.findById(2L)).thenReturn(Optional.of(alvo));
        when(repo.countByRole(Roles.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> service.excluir(2L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("O sistema precisa de pelo menos um administrador.");

        verify(repo, never()).delete(any(Usuario.class));
    }

    @Test
    void excluirUserPorOutroAtorChamaDelete() {
        Usuario alvo = usuario(2L, "Maria", "maria@x.com", "123", "sub-2", Roles.USER);

        when(repo.findById(2L)).thenReturn(Optional.of(alvo));

        service.excluir(2L, 1L);

        verify(repo).delete(alvo);
    }

    @Test
    void excluirUsuarioComumChamaDeleteDeComentariosAntesDoDelete() {
        Usuario alvo = usuario(2L, "Maria", "maria@x.com", "123", "sub-2", Roles.USER);

        when(repo.findById(2L)).thenReturn(Optional.of(alvo));

        service.excluir(2L, 1L);

        InOrder inOrder = inOrder(comentarioRepository, repo);
        inOrder.verify(comentarioRepository).deleteByAutorId(2L);
        inOrder.verify(repo).delete(alvo);
    }

    @Test
    void excluirASiMesmoNaoChamaDeleteDeComentarios() {
        Usuario proprio = usuario(1L, "Admin", "admin@x.com", "123", "sub-1", Roles.ADMIN);

        when(repo.findById(1L)).thenReturn(Optional.of(proprio));

        assertThatThrownBy(() -> service.excluir(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Você não pode excluir a sua própria conta.");

        verify(comentarioRepository, never()).deleteByAutorId(any(Long.class));
    }

    @Test
    void excluirUnicoAdminNaoChamaDeleteDeComentarios() {
        Usuario alvo = usuario(2L, "Admin", "admin@x.com", "123", "sub-2", Roles.ADMIN);

        when(repo.findById(2L)).thenReturn(Optional.of(alvo));
        when(repo.countByRole(Roles.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> service.excluir(2L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("O sistema precisa de pelo menos um administrador.");

        verify(comentarioRepository, never()).deleteByAutorId(any(Long.class));
    }

    @Test
    void usuarioComRoleNuloPodeSerEditadoEExcluido() {
        Usuario alvo = usuario(2L, "Sem Perfil", "sem@x.com", "123", "sub-2", null);
        UsuarioAdminForm form = form("  Sem Perfil Editado  ", Roles.USER);

        when(repo.findById(2L)).thenReturn(Optional.of(alvo));
        when(repo.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario atualizado = service.atualizar(2L, form, 1L);
        assertThat(atualizado.getNome()).isEqualTo("Sem Perfil Editado");
        assertThat(atualizado.getRole()).isEqualTo(Roles.USER);

        service.excluir(2L, 1L);

        verify(repo).save(alvo);
        verify(repo).delete(alvo);
    }

}
