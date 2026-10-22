package com.example.b_eletricar.services;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;

@Service
public class UsuarioSocialService {

    private final UsuarioRepository repo;

    public UsuarioSocialService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario salvarOuAtualizar(OidcUser oidc) {
        String sub = oidc.getSubject();
        String email = oidc.getEmail() == null ? null : oidc.getEmail().trim().toLowerCase();

        Usuario usuario = repo.findBySubId(sub).orElse(null);

        // não achou pelo sub: pode ser uma conta criada pelo formulário
        if (usuario == null && email != null) {
            usuario = repo.findByEmail(email).orElse(null);

            if (usuario != null && !Boolean.TRUE.equals(oidc.getEmailVerified())) {
                throw new OAuth2AuthenticationException(new OAuth2Error("email_nao_verificado"));
            }
        }

        if (usuario == null) {
            usuario = new Usuario();
        }
        if (usuario.getRole() == null) {
            usuario.setRole(Roles.USER);
        }

        usuario.setSubId(sub);
        usuario.setEmail(email);
        usuario.setNome(oidc.getFullName());
        return repo.save(usuario);
    }
}
