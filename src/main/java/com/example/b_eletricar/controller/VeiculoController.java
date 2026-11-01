package com.example.b_eletricar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;

import java.util.List;

import com.example.b_eletricar.dto.AvaliacaoForm;
import com.example.b_eletricar.services.AvaliacaoService;
import com.example.b_eletricar.services.VeiculoService;


@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;
    private final AvaliacaoService avaliacaoService;

    public VeiculoController(VeiculoService veiculoService, AvaliacaoService avaliacaoService) {
        this.veiculoService = veiculoService;
        this.avaliacaoService = avaliacaoService;
    }   

    // Endpoint para busca de veículo por id
    @GetMapping("/{id}")
    public String buscarVeiculoPorId(@PathVariable Long id, Model model) {
        var veiculo = veiculoService.buscarVeiculoPorId(id);
        if (veiculo.isPresent()) {
            model.addAttribute("veiculo", veiculo.get());
            var comentarios = avaliacaoService.listarPorVeiculo(id);
            model.addAttribute("comentarios", comentarios);
            model.addAttribute("mediaAvaliacoes", avaliacaoService.media(comentarios));
            model.addAttribute("avaliacaoForm", new AvaliacaoForm());
            return "detalhes-veiculo";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado.");
    }
    

    // Endpoint para a listagem de veículos com ou sem filtro e/ou palavra chave
    @GetMapping("/listagem")
    public String buscarVeiculo(
        @RequestParam(required = false) String marca, // Marca
        @RequestParam(required = false) String modelo, // Modelo
        @RequestParam(required = false) String carroceria, // Carroceria
        @RequestParam(required = false) Double precoMin,
        @RequestParam(required = false) Double precoMax, // Preço
        @RequestParam(required = false) Integer autonomiaKmMin,
        @RequestParam(required = false) Integer autonomiaKmMax, // Autonomia
        @RequestParam(required = false) Double capacidadeBateriaKwhMin,
        @RequestParam(required = false) Double capacidadeBateriaKwhMax, // Capacidade da bateria
        @RequestParam(required = false) Integer potenciaCvMin,
        @RequestParam(required = false) Integer potenciaCvMax, // Potência
        @RequestParam(required = false) Double tempoCargaHorasMin,
        @RequestParam(required = false) Double tempoCargaHorasMax, // Tempo de carga(
        @RequestParam(required = false) String palavraChave, // Palavra chave
        Model model) { 

        try {   model.addAttribute("veiculos", veiculoService.filtrarVeiculos(
                marca, modelo, carroceria, precoMin, precoMax, 
                autonomiaKmMin, autonomiaKmMax, capacidadeBateriaKwhMin, capacidadeBateriaKwhMax, 
                potenciaCvMin, potenciaCvMax, tempoCargaHorasMin, tempoCargaHorasMax, palavraChave
            ));
        } catch (IllegalArgumentException e) {
                model.addAttribute("erro", e.getMessage());
                model.addAttribute("veiculos", List.of());
            }

        return "home";
    }
    
    

    // Endpoint para redirecionar para os dados do fornecedor de um veículo
    @GetMapping("/{id}/fornecedor")
    public String buscarFornecedor(@PathVariable("id") Long id, Model model) {
       var fornecedor = veiculoService.buscarFornecedorPorVeiculoId(id);
        if (fornecedor.isEmpty()) {
            return "redirect:/";
        }
        String site = fornecedor.get().getSite();
        if (site == null || site.isBlank()) {
            return "redirect:/";
        }
        return "redirect:" + site;
    }
}