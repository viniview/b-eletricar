package com.example.b_eletricar.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.dto.VeiculoOpcao;
import com.example.b_eletricar.services.Comparar;
import com.example.b_eletricar.services.VeiculoService;

@Controller
public class ComparadorController {

    private final VeiculoService veiculoService;
    private final Comparar comparador;

    public ComparadorController(VeiculoService veiculoService, Comparar comparador) {
        this.veiculoService = veiculoService;
        this.comparador = comparador;
    }

    @GetMapping("/comparar")
    public String comparar(@RequestParam(required = false) Long carro1,
                           @RequestParam(required = false) Long carro2,
                           @RequestParam(required = false) Long carro3,
                           Model model) {
        List<Long> idsPorSlot = Arrays.asList(carro1, carro2, carro3);
        List<Long> ids = idsPorSlot.stream()
                .filter(id -> id != null)
                .toList();
        List<Veiculo> selecionados = new ArrayList<>();
        Veiculo[] veiculosPorSlot = new Veiculo[3];
        String erro = null;

        if (ids.size() != ids.stream().distinct().count()) {
            erro = "Selecione veículos diferentes para comparar.";
        } else {
            for (int indice = 0; indice < idsPorSlot.size(); indice++) {
                Long id = idsPorSlot.get(indice);
                if (id == null) {
                    continue;
                }
                var veiculo = veiculoService.buscarVeiculoPorId(id);
                if (veiculo.isEmpty()) {
                    erro = "Um dos veículos selecionados não foi encontrado.";
                    selecionados.clear();
                    Arrays.fill(veiculosPorSlot, null);
                    break;
                }
                selecionados.add(veiculo.get());
                veiculosPorSlot[indice] = veiculo.get();
            }
            if (selecionados.size() == 1) {
                erro = "Selecione ao menos 2 veículos para comparar.";
            }
        }

        List<Slot> slots = List.of(
                new Slot(1, "Primeiro carro", veiculosPorSlot[0]),
                new Slot(2, "Segundo carro", veiculosPorSlot[1]),
                new Slot(3, "Terceiro carro", veiculosPorSlot[2]));
        model.addAttribute("slots", slots);
        model.addAttribute("podeComparar", selecionados.size() >= 2 && erro == null);
        model.addAttribute("resultados", selecionados.size() >= 2 && erro == null
                ? comparador.comparar(selecionados) : null);
        model.addAttribute("erro", erro);
        return "comparar";
    }

    @GetMapping("/api/comparador")
    @ResponseBody
    public List<VeiculoOpcao> buscarOpcoes(@RequestParam(required = false) String marca,
                                            @RequestParam(required = false) String modelo,
                                            @RequestParam(required = false) String carroceria,
                                            @RequestParam(required = false) String busca) {
        return veiculoService.buscarParaComparacao(marca, modelo, carroceria, busca)
                .stream()
                .map(veiculo -> new VeiculoOpcao(veiculo.getId(), veiculo.getMarca(),
                        veiculo.getModelo(), veiculo.getCarroceria(), veiculo.getPreco()))
                .toList();
    }

    public record Slot(int numero, String titulo, Veiculo veiculo) {
    }
}
