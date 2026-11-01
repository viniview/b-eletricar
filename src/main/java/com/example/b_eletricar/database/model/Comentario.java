package com.example.b_eletricar.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
@Getter @Setter
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String texto;

    @Column
    private Integer nota;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario autor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @PrePersist
    void preencherData() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
}
