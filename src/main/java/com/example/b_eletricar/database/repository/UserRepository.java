package com.example.b_eletricar.database.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.b_eletricar.database.model.Visitante;

@Repository
public interface UserRepository extends CrudRepository <Visitante, String> {
    
    Optional<Visitante> findByUserName(String userName);
}
