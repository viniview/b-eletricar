package com.example.b_eletricar.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.example.b_eletricar.database.model.Usuario;

public class UsuarioPrincipal extends DefaultOidcUser implements UsuarioLogado {

    private final Usuario usuario;

    public UsuarioPrincipal(Collection<? extends GrantedAuthority> authorities,
                            OidcUser oidc, Usuario usuario) {
        super(authorities, oidc.getIdToken(), oidc.getUserInfo(), "sub");
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}