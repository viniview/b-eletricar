package com.example.b_eletricar.database.repository;

import com.example.b_eletricar.database.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long>, JpaSpecificationExecutor<Veiculo> {

    List<Veiculo> findByModeloContainingIgnoreCase(String modelo);

    public List<Veiculo> findByFornecedor(com.example.b_eletricar.database.model.MarcaVeiculo fornecedor);
    
}
