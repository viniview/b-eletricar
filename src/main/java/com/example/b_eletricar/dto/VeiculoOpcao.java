package com.example.b_eletricar.dto;

public record VeiculoOpcao(
        Long id,
        String marca,
        String modelo,
        String carroceria,
        Double preco,
        String fotoUrl) {
}
