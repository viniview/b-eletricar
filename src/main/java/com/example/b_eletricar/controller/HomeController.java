package com.example.b_eletricar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.example.b_eletricar.services.VeiculoService;

@Controller
public class HomeController {

    private final VeiculoService veiculoService;

    public HomeController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping("/")
    public String home(Model model) {

    model.addAttribute("veiculos", veiculoService.listarTodos());
        return "home";
    }
    
}
