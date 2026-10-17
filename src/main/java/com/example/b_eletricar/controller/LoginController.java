package com.example.b_eletricar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ui.Model;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.security.UsuarioPrincipal;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return "redirect:/perfil/" + principal.getUsuario().getId();
    }
    @GetMapping("/perfil/{id}")
    public String redirecionarPerfilComId(@PathVariable Long id,
                                         @AuthenticationPrincipal UsuarioPrincipal principal,
                                         Model model) {
        boolean dono = principal.getUsuario().getId().equals(id);
        boolean admin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!dono && !admin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "perfil";
    }
}
