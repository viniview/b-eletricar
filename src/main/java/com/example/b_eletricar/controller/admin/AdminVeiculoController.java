package com.example.b_eletricar.controller.admin;

//j
import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.b_eletricar.dto.VeiculoForm;
import com.example.b_eletricar.services.AdminVeiculoService;
import com.example.b_eletricar.services.FornecedorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/veiculos")
public class AdminVeiculoController {

    private final AdminVeiculoService veiculoService;
    private final FornecedorService fornecedorService;

    public AdminVeiculoController(AdminVeiculoService veiculoService, FornecedorService fornecedorService) {
        this.veiculoService = veiculoService;
        this.fornecedorService = fornecedorService;
    }


    @GetMapping("")
    public String listar(Model model) {
        model.addAttribute("veiculos", veiculoService.listar());
        return "admin/veiculos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new VeiculoForm());
        model.addAttribute("modo", "novo");
        model.addAttribute("fornecedores", fornecedorService.listar());
        return "admin/veiculos/form";
    }

    @PostMapping("")
    public String criar(@Valid @ModelAttribute("form") VeiculoForm form,
                        BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("modo", "novo");
            model.addAttribute("fornecedores", fornecedorService.listar());
            return "admin/veiculos/form";
        }

        try {
            veiculoService.criar(form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("modo", "novo");
            model.addAttribute("fornecedores", fornecedorService.listar());
            return "admin/veiculos/form";
        }

        redirect.addFlashAttribute("sucesso", "Veículo cadastrado.");
        return "redirect:/admin/veiculos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        VeiculoForm form = veiculoService.paraForm(veiculoService.buscarPorId(id));

        model.addAttribute("form", form);
        model.addAttribute("modo", "editar");
        model.addAttribute("veiculoId", id);
        model.addAttribute("fornecedores", fornecedorService.listar());
        return "admin/veiculos/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") VeiculoForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("modo", "editar");
            model.addAttribute("veiculoId", id);
            model.addAttribute("fornecedores", fornecedorService.listar());
            return "admin/veiculos/form";
        }

        try {
            veiculoService.atualizar(id, form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("modo", "editar");
            model.addAttribute("veiculoId", id);
            model.addAttribute("fornecedores", fornecedorService.listar());
            return "admin/veiculos/form";
        }

        redirect.addFlashAttribute("sucesso", "Veículo atualizado.");
        return "redirect:/admin/veiculos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            veiculoService.excluir(id);
        } catch (ResponseStatusException e) {
            redirect.addFlashAttribute("erro", e.getReason());
            return "redirect:/admin/veiculos";
        }

        redirect.addFlashAttribute("sucesso", "Veículo excluído.");
        return "redirect:/admin/veiculos";
    }

}
