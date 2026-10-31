package com.example.b_eletricar.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoForm {

    @NotNull(message = "Selecione uma nota.")
    @Min(value = 1, message = "A nota deve ser de 1 a 5.")
    @Max(value = 5, message = "A nota deve ser de 1 a 5.")
    private Integer nota;

    @NotBlank(message = "Escreva um comentário.")
    @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres.")
    private String texto;
}
