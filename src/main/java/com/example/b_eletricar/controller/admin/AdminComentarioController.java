package com.example.b_eletricar.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.b_eletricar.database.model.Comentario;
import com.example.b_eletricar.services.AdminVeiculoService;
import com.example.b_eletricar.services.ComentarioAdminService;

@Controller
@RequestMapping("/admin/comentarios")
public class AdminComentarioController {

    private final ComentarioAdminService comentarioService;
    private final AdminVeiculoService veiculoService;

    public AdminComentarioController(ComentarioAdminService comentarioService, AdminVeiculoService veiculoService) {
        this.comentarioService = comentarioService;
        this.veiculoService = veiculoService;
    }

    @GetMapping("")
    public String listar(@RequestParam(required = false) Long veiculoId,
                         @RequestParam(defaultValue = "0") int pagina,
                         Model model, RedirectAttributes redirect) {
        Page<Comentario> comentarios = comentarioService.listar(veiculoId, pagina);

        if (comentarios.isEmpty() && pagina > 0) {
            redirect.addAttribute("pagina", pagina - 1);
            if (veiculoId != null) {
                redirect.addAttribute("veiculoId", veiculoId);
            }
            return "redirect:/admin/comentarios";
        }

        model.addAttribute("pagina", comentarios);
        model.addAttribute("veiculos", veiculoService.listar());
        model.addAttribute("veiculoId", veiculoId);
        return "admin/comentarios/lista";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          @RequestParam(required = false) Long veiculoId,
                          @RequestParam(required = false) Integer pagina,
                          RedirectAttributes redirect) {
        comentarioService.excluir(id);

        redirect.addFlashAttribute("sucesso", "Comentário excluído.");
        if (veiculoId != null) {
            redirect.addAttribute("veiculoId", veiculoId);
        }
        if (pagina != null) {
            redirect.addAttribute("pagina", pagina);
        }
        return "redirect:/admin/comentarios";
    }

}
