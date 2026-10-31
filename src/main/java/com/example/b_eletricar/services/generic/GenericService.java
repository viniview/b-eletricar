package com.example.b_eletricar.services.generic;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class GenericService<T, ID> {

    // As subclasses concretas informarão qual Repository usar
    protected abstract JpaRepository<T, ID> getRepository();

}