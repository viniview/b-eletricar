package com.example.b_eletricar.services;

import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.model.Proposta;
import com.example.b_eletricar.database.repository.VeiculoRepository;
import com.example.b_eletricar.database.repository.PropostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PropostaRepository propostaRepository;

    public List<Veiculo> listarCatalogo() {
        return veiculoRepository.findAll();
    }

    public List<Veiculo> filtrarPorEspecificacoes(Specification<Veiculo> spec) {
        return veiculoRepository.findAll(spec);
    }

    @Transactional
    public Proposta criarSolicitacao(String nome, String email, String telefone, String tipo, Long veiculoId) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        Proposta proposta = new Proposta();
        proposta.setNomeVisitante(nome);
        proposta.setEmailVisitante(email);
        proposta.setTelefoneVisitante(telefone);
        proposta.setTipoSolicitacao(tipo);
        proposta.setDataSolicitacao(LocalDateTime.now());
        proposta.setStatus("PENDENTE");
        proposta.setVeiculo(veiculo);

        return propostaRepository.save(proposta);
    }

    @Transactional
    public Veiculo salvarVeiculo(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    public Veiculo buscarPorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com o ID: " + id));
    }

    @Transactional
    public Veiculo atualizarEstoque(Long veiculoId, Integer novaQuantidade) {
        Veiculo veiculo = buscarPorId(veiculoId);
        veiculo.setQuantidadeEstoque(novaQuantidade);
        return veiculoRepository.save(veiculo);
    }
}