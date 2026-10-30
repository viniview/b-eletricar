package com.example.b_eletricar.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.b_eletricar.dto.FornecedorForm;
import com.example.b_eletricar.services.FornecedorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/fornecedores")
public class AdminFornecedorController {

    private final FornecedorService fornecedorService;

    public AdminFornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping("")
    public String listar(Model model) {
        model.addAttribute("fornecedores", fornecedorService.listar());
        return "admin/fornecedores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new FornecedorForm());
        model.addAttribute("modo", "novo");
        return "admin/fornecedores/form";
    }

    @PostMapping("")
    public String criar(@Valid @ModelAttribute("form") FornecedorForm form,
                        BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("modo", "novo");
            return "admin/fornecedores/form";
        }

        try {
            fornecedorService.criar(form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("modo", "novo");
            return "admin/fornecedores/form";
        }

        redirect.addFlashAttribute("sucesso", "Fornecedor cadastrado.");
        return "redirect:/admin/fornecedores";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        FornecedorForm form = fornecedorService.paraForm(fornecedorService.buscarPorId(id));

        model.addAttribute("form", form);
        model.addAttribute("modo", "editar");
        model.addAttribute("fornecedorId", id);
        return "admin/fornecedores/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") FornecedorForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("modo", "editar");
            model.addAttribute("fornecedorId", id);
            return "admin/fornecedores/form";
        }

        try {
            fornecedorService.atualizar(id, form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("modo", "editar");
            model.addAttribute("fornecedorId", id);
            return "admin/fornecedores/form";
        }

        redirect.addFlashAttribute("sucesso", "Fornecedor atualizado.");
        return "redirect:/admin/fornecedores";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            fornecedorService.excluir(id);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/admin/fornecedores";
        }

        redirect.addFlashAttribute("sucesso", "Fornecedor excluído.");
        return "redirect:/admin/fornecedores";
    }

}
