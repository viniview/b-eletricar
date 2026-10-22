package com.example.b_eletricar.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.security.UsuarioDetails;
import com.example.b_eletricar.security.UsuarioLogado;

@Controller
public class SenhaController {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public SenhaController(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping("/definir-senha")
    public String formulario(@AuthenticationPrincipal UsuarioLogado principal, Model model) {
        preencher(principal, model);
        return "definir-senha";
    }

    @PostMapping("/definir-senha")
    public String salvar(@RequestParam(required = false) String senhaAtual,
                         @RequestParam String senha,
                         @RequestParam String confirmacao,
                         @AuthenticationPrincipal UsuarioLogado principal,
                         Model model) {

        Usuario u = repo.findById(principal.getUsuario().getId()).orElseThrow();
        boolean exigeAtual = u.getSenha() != null && principal instanceof UsuarioDetails;

        String erro = null;
        if (exigeAtual && (senhaAtual == null || !encoder.matches(senhaAtual, u.getSenha()))) {
            erro = "Senha atual incorreta.";
        } else if (senha.length() < 8 || senha.length() > 72) {
            erro = "A senha deve ter de 8 a 72 caracteres.";
        } else if (!senha.equals(confirmacao)) {
            erro = "A confirmação não coincide com a senha.";
        }

        if (erro != null) {
            preencher(principal, model);
            model.addAttribute("erro", erro);
            return "definir-senha";
        }

        u.setSenha(encoder.encode(senha));
        repo.save(u);
        return "redirect:/perfil/" + u.getId() + "?senhaDefinida";
    }

    private void preencher(UsuarioLogado principal, Model model) {
        Usuario u = repo.findById(principal.getUsuario().getId()).orElseThrow();
        model.addAttribute("temSenha", u.getSenha() != null);
        model.addAttribute("exigeAtual", u.getSenha() != null && principal instanceof UsuarioDetails);
    }
}