package com.example.b_eletricar.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.dto.UsuarioAdminForm;
import com.example.b_eletricar.security.UsuarioLogado;
import com.example.b_eletricar.services.UsuarioAdminService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioAdminService usuarioAdminService;

    public AdminUsuarioController(UsuarioAdminService usuarioAdminService) {
        this.usuarioAdminService = usuarioAdminService;
    }

    @GetMapping("")
    public String listar(Model model, @AuthenticationPrincipal UsuarioLogado principal) {
        model.addAttribute("usuarios", usuarioAdminService.listar());
        model.addAttribute("idLogado", principal.getUsuario().getId());
        return "admin/usuarios/lista";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal UsuarioLogado principal) {
        Long idLogado = principal.getUsuario().getId();
        Usuario usuario = usuarioAdminService.buscarPorId(id);

        model.addAttribute("form", usuarioAdminService.paraForm(usuario));
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioId", id);
        model.addAttribute("proprio", id.equals(idLogado));
        return "admin/usuarios/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") UsuarioAdminForm form,
                            BindingResult result, Model model, RedirectAttributes redirect,
                            @AuthenticationPrincipal UsuarioLogado principal) {
        Long idLogado = principal.getUsuario().getId();

        if (result.hasErrors()) {
            preencherForm(model, id, idLogado);
            return "admin/usuarios/form";
        }

        try {
            usuarioAdminService.atualizar(id, form, idLogado);
        } catch (IllegalArgumentException e) {
            preencherForm(model, id, idLogado);
            model.addAttribute("erro", e.getMessage());
            return "admin/usuarios/form";
        }

        redirect.addFlashAttribute("sucesso", "Usuário atualizado.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect,
                          @AuthenticationPrincipal UsuarioLogado principal) {
        Long idLogado = principal.getUsuario().getId();

        try {
            usuarioAdminService.excluir(id, idLogado);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/admin/usuarios";
        }

        redirect.addFlashAttribute("sucesso", "Usuário excluído.");
        return "redirect:/admin/usuarios";
    }

    private void preencherForm(Model model, Long id, Long idLogado) {
        Usuario usuario = usuarioAdminService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioId", id);
        model.addAttribute("proprio", id.equals(idLogado));
    }

}
