package com.example.b_eletricar.dto;

import com.example.b_eletricar.database.model.enums.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioAdminForm {

    @NotBlank(message = "Informe o nome")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "Selecione o perfil")
    private Roles role;
}
