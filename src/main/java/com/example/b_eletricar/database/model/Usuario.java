package com.example.b_eletricar.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.example.b_eletricar.database.model.enums.Roles;

@Entity
@Table(name = "usuario")
@Getter @Setter 
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String senha;

    private String nome;

    @Enumerated (EnumType.STRING)
    @Column(nullable = true)
    private Roles role;
    
    @Column(unique = true)
    private String subId;
}