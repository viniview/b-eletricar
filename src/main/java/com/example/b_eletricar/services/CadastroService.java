package com.example.b_eletricar.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.dto.CadastroForm;

@Service
public class CadastroService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public CadastroService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Usuario cadastrar(CadastroForm form) {
        String email = form.getEmail().trim().toLowerCase();

        Optional<Usuario> existente = repo.findByEmail(email);
        if (existente.isPresent()) {
            if (existente.get().getSenha() == null) {
                throw new IllegalArgumentException(
                    "Este e-mail já tem conta via Google. Entre com o Google e defina uma senha no perfil.");
            }
            throw new IllegalArgumentException(
                "E-mail já cadastrado. Entre com seu e-mail e senha.");
        }

        Usuario u = new Usuario();
        u.setNome(form.getNome().trim());
        u.setEmail(email);
        u.setSenha(encoder.encode(form.getSenha()));
        u.setRole(Roles.USER);
        return repo.save(u);
    }
}