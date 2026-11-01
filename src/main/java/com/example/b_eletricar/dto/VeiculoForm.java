package com.example.b_eletricar.dto;

//j
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VeiculoForm {

    @NotBlank(message = "Informe a marca")
    @Size(max = 50, message = "A marca deve ter no máximo 50 caracteres")
    private String marca;

    @NotBlank(message = "Informe o modelo")
    @Size(max = 50, message = "O modelo deve ter no máximo 50 caracteres")
    private String modelo;

    @Size(max = 50, message = "A carroceria deve ter no máximo 50 caracteres")
    private String carroceria;

    @NotNull(message = "Informe o preço")
    @Positive(message = "Informe um preço maior que zero")
    private Double preco;

    @NotNull(message = "Informe a autonomia")
    @Min(value = 1, message = "A autonomia deve ser no mínimo 1 km")
    @Max(value = 2000, message = "A autonomia deve ser no máximo 2000 km")
    private Integer autonomiaKm;

    @NotNull(message = "Informe a capacidade da bateria")
    @Positive(message = "A capacidade da bateria deve ser maior que zero")
    @Max(value = 300, message = "A capacidade da bateria deve ser no máximo 300 kWh")
    private Double capacidadeBateriaKwh;

    @Positive(message = "A potência deve ser maior que zero")
    private Integer potenciaCv;

    @Positive(message = "O tempo de carga deve ser maior que zero")
    private Double tempoCargaHoras;


    private org.springframework.web.multipart.MultipartFile foto;
    private String fotoAtual; 


    @NotNull(message = "Selecione um fornecedor")
    private Long fornecedorId;
}
