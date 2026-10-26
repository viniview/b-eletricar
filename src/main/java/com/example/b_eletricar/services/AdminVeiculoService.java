package com.example.b_eletricar.services;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.b_eletricar.database.model.Fornecedor;
import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.repository.FornecedorRepository;
import com.example.b_eletricar.database.repository.VeiculoRepository;
import com.example.b_eletricar.dto.VeiculoForm;

@Service
public class AdminVeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final FornecedorRepository fornecedorRepository;

    public AdminVeiculoService(VeiculoRepository veiculoRepository, FornecedorRepository fornecedorRepository) {
        this.veiculoRepository = veiculoRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    public List<Veiculo> listar() {
        return veiculoRepository.findAll(Sort.by("marca").and(Sort.by("modelo")));
    }

    public Veiculo buscarPorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado."));
    }

    public VeiculoForm paraForm(Veiculo v) {
        VeiculoForm form = new VeiculoForm();
        form.setMarca(v.getMarca());
        form.setModelo(v.getModelo());
        form.setCarroceria(v.getCarroceria());
        form.setPreco(v.getPreco());
        form.setAutonomiaKm(v.getAutonomiaKm());
        form.setCapacidadeBateriaKwh(v.getCapacidadeBateriaKwh());
        form.setPotenciaCv(v.getPotenciaCv());
        form.setTempoCargaHoras(v.getTempoCargaHoras());
        form.setFornecedorId(v.getFornecedor() != null ? v.getFornecedor().getId() : null);
        return form;
    }

    @Transactional
    public Veiculo criar(VeiculoForm form) {
        Veiculo v = new Veiculo();
        copiar(form, v);
        return veiculoRepository.save(v);
    }

    @Transactional
    public Veiculo atualizar(Long id, VeiculoForm form) {
        Veiculo v = buscarPorId(id);
        copiar(form, v);
        return veiculoRepository.save(v);
    }

    @Transactional
    public void excluir(Long id) {
        Veiculo v = buscarPorId(id);
        veiculoRepository.delete(v);
    }

    private void copiar(VeiculoForm form, Veiculo v) {
        v.setMarca(form.getMarca().trim());
        v.setModelo(form.getModelo().trim());
        v.setCarroceria(normalizaOpcional(form.getCarroceria()));
        v.setPreco(form.getPreco());
        v.setAutonomiaKm(form.getAutonomiaKm());
        v.setCapacidadeBateriaKwh(form.getCapacidadeBateriaKwh());
        v.setPotenciaCv(form.getPotenciaCv());
        v.setTempoCargaHoras(form.getTempoCargaHoras());
        v.setFornecedor(fornecedorPorId(form.getFornecedorId()));
    }

    private Fornecedor fornecedorPorId(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor inválido."));
    }

    private String normalizaOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

}
