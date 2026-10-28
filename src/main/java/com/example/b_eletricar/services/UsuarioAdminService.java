package com.example.b_eletricar.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.ComentarioRepository;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.dto.UsuarioAdminForm;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository repo;
    private final ComentarioRepository comentarioRepository;

    public UsuarioAdminService(UsuarioRepository repo, ComentarioRepository comentarioRepository) {
        this.repo = repo;
        this.comentarioRepository = comentarioRepository;
    }

    public List<Usuario> listar() {
        return repo.findAllByOrderByNomeAsc();
    }

    public Usuario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    public UsuarioAdminForm paraForm(Usuario u) {
        UsuarioAdminForm form = new UsuarioAdminForm();
        form.setNome(u.getNome());
        form.setRole(u.getRole());
        return form;
    }

    @Transactional
    public Usuario atualizar(Long id, UsuarioAdminForm form, Long idAtor) {
        Usuario u = buscarPorId(id);
        String nome = form.getNome().trim();
        Roles novoRole = form.getRole();

        if (id.equals(idAtor) && novoRole != Roles.ADMIN) {
            throw new IllegalArgumentException("Você não pode remover o seu próprio perfil de administrador.");
        }

        if (u.getRole() == Roles.ADMIN && novoRole != Roles.ADMIN && repo.countByRole(Roles.ADMIN) <= 1) {
            throw new IllegalArgumentException("O sistema precisa de pelo menos um administrador.");
        }

        u.setNome(nome);
        u.setRole(novoRole);
        return repo.save(u);
    }

    @Transactional
    public void excluir(Long id, Long idAtor) {
        Usuario u = buscarPorId(id);

        if (id.equals(idAtor)) {
            throw new IllegalStateException("Você não pode excluir a sua própria conta.");
        }

        if (u.getRole() == Roles.ADMIN && repo.countByRole(Roles.ADMIN) <= 1) {
            throw new IllegalStateException("O sistema precisa de pelo menos um administrador.");
        }

        comentarioRepository.deleteByAutorId(id);
        repo.delete(u);
    }
}
