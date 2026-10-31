package com.example.b_eletricar.database.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter; 
import lombok.AllArgsConstructor;  
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fornecedor")
@Getter @Setter 
@AllArgsConstructor 
@NoArgsConstructor
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String emailContato;
    private String site;

}
