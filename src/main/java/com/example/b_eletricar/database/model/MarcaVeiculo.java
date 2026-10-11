package com.example.b_eletricar.database.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedor")
public class MarcaVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String emailContato;
    private String site;

}