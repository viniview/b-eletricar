package com.example.b_eletricar.services;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class UsuarioSocialService {

    private final UsuarioRepository repo;

    public UsuarioSocialService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario salvarOuAtualizar(OidcUser oidc) {
        Usuario usuario = repo.findBySubId(oidc.getSubject())
                .orElseGet(Usuario::new);

        usuario.setSubId(oidc.getSubject());
        usuario.setEmail(oidc.getEmail());
        usuario.setNome(oidc.getFullName());

        if (usuario.getRole() == null) {
            usuario.setRole(Roles.USER);
        }
        return repo.save(usuario);
    }
}
