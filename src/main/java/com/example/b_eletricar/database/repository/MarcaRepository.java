package com.example.b_eletricar.database.repository;

import com.example.b_eletricar.database.model.MarcaVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRepository extends JpaRepository<MarcaVeiculo, Long> { }