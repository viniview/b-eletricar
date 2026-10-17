package com.example.b_eletricar.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.services.UsuarioSocialService;

@Service
public class CustomOidcUser extends OidcUserService {

    private final UsuarioSocialService usuarioSocialService;

    public CustomOidcUser(UsuarioSocialService usuarioSocialService) {
        this.usuarioSocialService = usuarioSocialService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        // 1. O Spring busca os dados do usuário no Google (OIDC)
        OidcUser oidcUser = super.loadUser(request);

        // 2. Persiste/atualiza o usuário local pela claim `sub`
        Usuario usuario = usuarioSocialService.salvarOuAtualizar(oidcUser);

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));        // 3. Devolve o usuário para o Spring continuar o login
        return new UsuarioPrincipal(authorities, oidcUser, usuario);
    }
}
