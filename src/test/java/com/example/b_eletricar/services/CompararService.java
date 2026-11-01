package com.example.b_eletricar.services;

import com.example.b_eletricar.database.model.Veiculo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompararTest {

    private final Comparar comparar = new Comparar();

    @Test
    void identificaOMelhorVeiculoEmCadaAtributo() {
        Veiculo carroA = veiculo("Marca A", "Modelo A", 150_000.0, 300, 50.0, 150, 8.0);
        Veiculo carroB = veiculo("Marca B", "Modelo B", 130_000.0, 450, 60.0, 120, 6.0);

        List<Comparar.ResultadoAtributo> resultados = comparar.comparar(List.of(carroA, carroB));

        assertThat(resultados).hasSize(5);
        assertThat(resultados.get(0).vencedores()).containsExactly("Marca B Modelo B");
        assertThat(resultados.get(1).vencedores()).containsExactly("Marca B Modelo B");
        assertThat(resultados.get(2).vencedores()).containsExactly("Marca B Modelo B");
        assertThat(resultados.get(3).vencedores()).containsExactly("Marca A Modelo A");
        assertThat(resultados.get(4).vencedores()).containsExactly("Marca B Modelo B");
    }

    @Test
    void ignoraAtributosSemDadosEIdentificaEmpate() {
        Veiculo carroA = veiculo("Marca A", "Modelo A", null, 300, null, null, null);
        Veiculo carroB = veiculo("Marca B", "Modelo B", null, 300, null, null, null);

        List<Comparar.ResultadoAtributo> resultados = comparar.comparar(List.of(carroA, carroB));

        assertThat(resultados.get(0).vencedores()).isEmpty();
        assertThat(resultados.get(0).valores())
                .extracting(Comparar.ValorVeiculo::valor)
                .containsExactly("Sem dados", "Sem dados");
        assertThat(resultados.get(1).vencedores())
                .containsExactly("Marca A Modelo A", "Marca B Modelo B");
    }

    private Veiculo veiculo(String marca, String modelo, Double preco, Integer autonomia,
                            Double bateria, Integer potencia, Double tempoCarga) {
        Veiculo veiculo = new Veiculo();
        veiculo.setMarca(marca);
        veiculo.setModelo(modelo);
        veiculo.setPreco(preco);
        veiculo.setAutonomiaKm(autonomia);
        veiculo.setCapacidadeBateriaKwh(bateria);
        veiculo.setPotenciaCv(potencia);
        veiculo.setTempoCargaHoras(tempoCarga);
        return veiculo;
    }
}