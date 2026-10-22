package com.example.b_eletricar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.b_eletricar.dto.CadastroForm;
import com.example.b_eletricar.services.CadastroService;

import jakarta.validation.Valid;

@Controller
public class CadastroController {

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @GetMapping("/cadastro")
    public String formulario(Model model) {
        model.addAttribute("form", new CadastroForm());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("form") CadastroForm form,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "cadastro";
        }
        try {
            cadastroService.cadastrar(form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }
        return "redirect:/login?cadastrado";
    }
}