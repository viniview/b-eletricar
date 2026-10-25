package com.example.b_eletricar.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.b_eletricar.database.model.Fornecedor;
import com.example.b_eletricar.database.repository.FornecedorRepository;
import com.example.b_eletricar.database.repository.VeiculoRepository;
import com.example.b_eletricar.dto.FornecedorForm;

@Service
public class FornecedorService {

    private final FornecedorRepository repo;
    private final VeiculoRepository veiculoRepository;

    public FornecedorService(FornecedorRepository repo, VeiculoRepository veiculoRepository) {
        this.repo = repo;
        this.veiculoRepository = veiculoRepository;
    }

    public List<Fornecedor> listar() {
        return repo.findAllByOrderByNomeAsc();
    }

    public Fornecedor buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado."));
    }

    public FornecedorForm paraForm(Fornecedor f) {
        FornecedorForm form = new FornecedorForm();
        form.setNome(f.getNome());
        form.setEmailContato(f.getEmailContato());
        form.setSite(f.getSite());
        return form;
    }

    @Transactional
    public Fornecedor criar(FornecedorForm form) {
        String nome = form.getNome().trim();

        if (repo.existsByNomeIgnoreCase(nome)) {
            throw new IllegalArgumentException("Já existe um fornecedor com esse nome.");
        }

        Fornecedor f = new Fornecedor();
        f.setNome(nome);
        f.setEmailContato(normalizaOpcional(form.getEmailContato()));
        f.setSite(normalizaOpcional(form.getSite()));
        return repo.save(f);
    }

    @Transactional
    public Fornecedor atualizar(Long id, FornecedorForm form) {
        Fornecedor f = buscarPorId(id);
        String nome = form.getNome().trim();

        if (repo.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new IllegalArgumentException("Já existe um fornecedor com esse nome.");
        }

        f.setNome(nome);
        f.setEmailContato(normalizaOpcional(form.getEmailContato()));
        f.setSite(normalizaOpcional(form.getSite()));
        return repo.save(f);
    }

    @Transactional
    public void excluir(Long id) {
        Fornecedor f = buscarPorId(id);

        if (veiculoRepository.existsByFornecedorId(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir: há veículos vinculados a este fornecedor.");
        }

        repo.delete(f);
    }

    private String normalizaOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

}
