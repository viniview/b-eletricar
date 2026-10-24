package com.example.b_eletricar.database.repository;

import com.example.b_eletricar.database.model.MarcaVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<MarcaVeiculo, Long> { }