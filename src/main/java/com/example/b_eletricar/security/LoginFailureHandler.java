package com.example.b_eletricar.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.b_eletricar.database.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final UsuarioRepository repo;

    public LoginFailureHandler(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String email = request.getParameter("username");

        // conta criada pelo Google que ainda não definiu senha
        boolean soGoogle = email != null && repo.findByEmail(email.trim().toLowerCase())
                .map(u -> u.getSenha() == null && u.getSubId() != null)
                .orElse(false);

        response.sendRedirect(soGoogle ? "/login?soGoogle" : "/login?error");
    }
}