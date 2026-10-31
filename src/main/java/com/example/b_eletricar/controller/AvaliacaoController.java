package com.example.b_eletricar.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.dto.AvaliacaoForm;
import com.example.b_eletricar.security.UsuarioLogado;
import com.example.b_eletricar.services.AvaliacaoService;
import com.example.b_eletricar.services.VeiculoService;

import jakarta.validation.Valid;

@Controller
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final VeiculoService veiculoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService, VeiculoService veiculoService) {
        this.avaliacaoService = avaliacaoService;
        this.veiculoService = veiculoService;
    }

    @PostMapping("/veiculos/{id}/avaliacoes")
    public String avaliar(@PathVariable Long id,
                          @Valid @ModelAttribute("avaliacaoForm") AvaliacaoForm form,
                          BindingResult bindingResult,
                          Authentication authentication,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        var veiculo = veiculoService.buscarVeiculoPorId(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Veículo não encontrado."));

        if (bindingResult.hasErrors()) {
            model.addAttribute("veiculo", veiculo);
            var comentarios = avaliacaoService.listarPorVeiculo(id);
            model.addAttribute("comentarios", comentarios);
            model.addAttribute("mediaAvaliacoes", avaliacaoService.media(comentarios));
            model.addAttribute("erroAvaliacao", "Confira a nota e o comentário antes de enviar.");
            return "detalhes-veiculo";
        }

        avaliacaoService.avaliar(id, emailDoUsuario(authentication), form);
        redirectAttributes.addFlashAttribute("sucessoAvaliacao", "Sua avaliação foi publicada.");
        return "redirect:/veiculos/" + id;
    }

    private String emailDoUsuario(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UsuarioLogado usuarioLogado) {
            Usuario usuario = usuarioLogado.getUsuario();
            return usuario.getEmail();
        }
        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getAttribute("email");
        }
        return authentication.getName();
    }
}
