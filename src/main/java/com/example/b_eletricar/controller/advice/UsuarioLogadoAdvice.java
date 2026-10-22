package com.example.b_eletricar.controller.advice;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

@ControllerAdvice
public class UsuarioLogadoAdvice {

    private final UsuarioRepository usuarioRepository;

    public UsuarioLogadoAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("usuarioLogado")
    public Usuario usuarioLogado(Authentication auth) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return null;
        }

        String email = auth.getPrincipal() instanceof OAuth2User u
                ? u.getAttribute("email")
                : auth.getName();

        if (email == null) {
            return null;
        }

        return usuarioRepository
                .findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElse(null);
    }
}