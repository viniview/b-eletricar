package com.example.b_eletricar.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ui.Model;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.security.UsuarioLogado;


@Controller
public class LoginController {

private final UsuarioRepository usuarioRepository;

public LoginController(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
}

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UsuarioLogado principal) {
        return "redirect:/perfil/" + principal.getUsuario().getId();
    }
    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable Long id,
                        @AuthenticationPrincipal UsuarioLogado principal,
                        Model model) {
        Usuario logado = principal.getUsuario();
        boolean dono = logado.getId().equals(id);
        boolean admin = logado.getRole() == Roles.ADMIN;

        if (!dono && !admin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("usuario", usuario);
        model.addAttribute("proprio", dono);
        model.addAttribute("temSenha", usuario.getSenha() != null);
        return "perfil";
    }
}
