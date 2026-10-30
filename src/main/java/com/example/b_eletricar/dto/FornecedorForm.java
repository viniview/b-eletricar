package com.example.b_eletricar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FornecedorForm {

    @NotBlank(message = "Informe o nome")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
    private String emailContato;

    @Pattern(regexp = "^(https?://.*)?$", message = "O site deve começar com http:// ou https://")
    @Size(max = 255, message = "O site deve ter no máximo 255 caracteres")
    private String site;
}
