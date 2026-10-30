package com.example.b_eletricar.database.repository;

import com.example.b_eletricar.database.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    Page<Comentario> findByVeiculoId(Long veiculoId, Pageable pageable);

    void deleteByVeiculoId(Long veiculoId);

    void deleteByAutorId(Long autorId);

}
