package com.example.b_eletricar.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.b_eletricar.database.model.Comentario;
import com.example.b_eletricar.database.repository.ComentarioRepository;

@Service
public class ComentarioAdminService {

    private static final int TAMANHO_PAGINA = 20;

    private final ComentarioRepository repo;

    public ComentarioAdminService(ComentarioRepository repo) {
        this.repo = repo;
    }

    public Page<Comentario> listar(Long veiculoId, int pagina) {
        int paginaValida = Math.max(pagina, 0);
        Pageable pageable = PageRequest.of(paginaValida, TAMANHO_PAGINA,
                Sort.by(Sort.Direction.DESC, "dataCriacao").and(Sort.by(Sort.Direction.DESC, "id")));

        return veiculoId == null ? repo.findAll(pageable) : repo.findByVeiculoId(veiculoId, pageable);
    }

    public Comentario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado."));
    }

    @Transactional
    public void excluir(Long id) {
        Comentario c = buscarPorId(id);
        repo.delete(c);
    }

}
