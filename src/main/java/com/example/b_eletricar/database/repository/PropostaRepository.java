package com.example.b_eletricar.database.repository;

import com.example.b_eletricar.database.model.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    List<Proposta> findByTipoSolicitacao(String tipoSolicitacao);
    
}