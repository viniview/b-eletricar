package com.example.b_eletricar.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public UsuarioDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = repo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (u.getSenha() == null || u.getRole() == null) {
            // conta criada só pelo Google: não tem login local
            throw new UsernameNotFoundException("Conta sem senha local");
        }
        return new UsuarioDetails(u);
    }
}