package com.example.b_eletricar.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.b_eletricar.database.model.Comentario;
import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.repository.ComentarioRepository;
import com.example.b_eletricar.database.repository.UsuarioRepository;
import com.example.b_eletricar.database.repository.VeiculoRepository;
import com.example.b_eletricar.dto.AvaliacaoForm;

@Service
public class AvaliacaoService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final VeiculoRepository veiculoRepository;

    public AvaliacaoService(ComentarioRepository comentarioRepository,
                            UsuarioRepository usuarioRepository,
                            VeiculoRepository veiculoRepository) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.veiculoRepository = veiculoRepository;
    }

    public List<Comentario> listarPorVeiculo(Long veiculoId) {
        return comentarioRepository.findByVeiculoIdOrderByDataCriacaoDesc(veiculoId);
    }

    public Double media(List<Comentario> comentarios) {
        var media = comentarios.stream()
                .map(Comentario::getNota)
                .filter(nota -> nota != null)
                .mapToInt(Integer::intValue)
                .average();
        return media.isPresent() ? media.getAsDouble() : null;
    }

    @Transactional
    public Comentario avaliar(Long veiculoId, String email, AvaliacaoForm form) {
        if (form.getNota() == null || form.getNota() < 1 || form.getNota() > 5
                || form.getTexto() == null || form.getTexto().isBlank()
                || form.getTexto().length() > 1000) {
            throw new IllegalArgumentException("Informe uma nota de 1 a 5 e um comentário de até 1000 caracteres.");
        }

        Usuario autor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "A conta autenticada não está cadastrada."));
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Veículo não encontrado."));

        Comentario comentario = new Comentario();
        comentario.setNota(form.getNota());
        comentario.setTexto(form.getTexto().trim());
        comentario.setAutor(autor);
        comentario.setVeiculo(veiculo);
        return comentarioRepository.save(comentario);
    }
}
