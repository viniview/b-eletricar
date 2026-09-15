package com.example.b_eletricar.controller;

import com.example.b_eletricar.services.VeiculoService;
import com.example.b_eletricar.database.model.Veiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    public String listarOuFiltrar(@RequestParam(required = false) String marca,
                                  @RequestParam(required = false) Integer autonomia,
                                  Model model) {
        
        List<Veiculo> veiculos;

        if (marca != null || autonomia != null) {
            veiculos = veiculoService.filtrarPorEspecificacoes(null);
        } else {
            veiculos = veiculoService.listarCatalogo();
        }

        model.addAttribute("veiculos", veiculos);
        return "catalogo";
    }

    @GetMapping("/{id}")
    public String detalharVeiculo(@PathVariable Long id, Model model) {
        Veiculo veiculo = veiculoService.buscarPorId(id);
        model.addAttribute("veiculo", veiculo);
        return "detalhes-veiculo";
    }

    @PostMapping("/proposta")
    public String solicitarProposta(@RequestParam String nome,
                                    @RequestParam String email,
                                    @RequestParam String telefone,
                                    @RequestParam Long veiculoId,
                                    RedirectAttributes redirectAttributes) {

        veiculoService.criarSolicitacao(nome, email, telefone, "PROPOSTA_COMPRA", veiculoId);
        
        redirectAttributes.addFlashAttribute("sucesso", "Sua proposta foi enviada com sucesso!");
        return "redirect:/veiculos/" + veiculoId;
    }

    @PostMapping("/test-drive")
    public String solicitarTestDrive(@RequestParam String nome,
                                     @RequestParam String email,
                                     @RequestParam String telefone,
                                     @RequestParam Long veiculoId,
                                     RedirectAttributes redirectAttributes) {

        veiculoService.criarSolicitacao(nome, email, telefone, "TEST_DRIVE", veiculoId);

        redirectAttributes.addFlashAttribute("sucesso", "Solicitação de Test-Drive agendada!");
        return "redirect:/veiculos/" + veiculoId;
    }

    @PostMapping("/admin/salvar")
    public String salvarVeiculoAdmin(@ModelAttribute Veiculo veiculo, RedirectAttributes redirectAttributes) {
        veiculoService.salvarVeiculo(veiculo);
        redirectAttributes.addFlashAttribute("mensagem", "Veículo salvo com sucesso!");
        return "redirect:/veiculos";
    }

    @PostMapping("/admin/estoque")
    public String atualizarEstoqueAdmin(@RequestParam Long veiculoId, 
                                        @RequestParam Integer quantidade,
                                        RedirectAttributes redirectAttributes) {
        veiculoService.atualizarEstoque(veiculoId, quantidade);
        redirectAttributes.addFlashAttribute("mensagem", "Estoque atualizado com sucesso!");
        return "redirect:/veiculos";
    }
}