package com.example.b_eletricar.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "veiculo")
@Getter @Setter
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(length = 50)
    private String carroceria;

    private Double preco;

    private Integer autonomiaKm;
    private Double capacidadeBateriaKwh;
    private Integer potenciaCv;
    private Double tempoCargaHoras;


    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;
}