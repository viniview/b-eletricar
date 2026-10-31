package com.example.b_eletricar.database.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

class VeiculoAccessorsTest {

    @Test
    void gettersESettersFuncionamParaTodosOsCampos() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("BYD");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(7L);
        veiculo.setMarca("BYD");
        veiculo.setModelo("Dolphin");
        veiculo.setCarroceria("Hatch");
        veiculo.setPreco(150000.0);
        veiculo.setAutonomiaKm(400);
        veiculo.setCapacidadeBateriaKwh(50.0);
        veiculo.setPotenciaCv(130);
        veiculo.setTempoCargaHoras(7.5);
        veiculo.setFotoUrl("https://example.com/foto.jpg");
        veiculo.setFornecedor(fornecedor);

        assertThat(veiculo.getId()).isEqualTo(7L);
        assertThat(veiculo.getMarca()).isEqualTo("BYD");
        assertThat(veiculo.getModelo()).isEqualTo("Dolphin");
        assertThat(veiculo.getCarroceria()).isEqualTo("Hatch");
        assertThat(veiculo.getPreco()).isEqualTo(150000.0);
        assertThat(veiculo.getAutonomiaKm()).isEqualTo(400);
        assertThat(veiculo.getCapacidadeBateriaKwh()).isEqualTo(50.0);
        assertThat(veiculo.getPotenciaCv()).isEqualTo(130);
        assertThat(veiculo.getTempoCargaHoras()).isEqualTo(7.5);
        assertThat(veiculo.getFotoUrl()).isEqualTo("https://example.com/foto.jpg");
        assertThat(veiculo.getFornecedor()).isSameAs(fornecedor);
        assertThat(fornecedor.getNome()).isEqualTo("BYD");
    }

    @Test
    void novoVeiculoComecaComTudoNulo() {
        Veiculo veiculo = new Veiculo();

        assertThat(veiculo.getId()).isNull();
        assertThat(veiculo.getMarca()).isNull();
        assertThat(veiculo.getModelo()).isNull();
        assertThat(veiculo.getCarroceria()).isNull();
        assertThat(veiculo.getPreco()).isNull();
        assertThat(veiculo.getAutonomiaKm()).isNull();
        assertThat(veiculo.getCapacidadeBateriaKwh()).isNull();
        assertThat(veiculo.getPotenciaCv()).isNull();
        assertThat(veiculo.getTempoCargaHoras()).isNull();
        assertThat(veiculo.getFotoUrl()).isNull();
        assertThat(veiculo.getFornecedor()).isNull();
    }

    @Test
    void mapeamentoJpaNaoFoiAlterado() {
        assertThat(Veiculo.class.getAnnotation(Entity.class)).isNotNull();

        Table tabela = Veiculo.class.getAnnotation(Table.class);
        assertThat(tabela).isNotNull();
        assertThat(tabela.name()).isEqualTo("veiculo");

        String[] campos = Arrays.stream(Veiculo.class.getDeclaredFields())
                .filter(campo -> !campo.isSynthetic())
                .map(Field::getName)
                .toArray(String[]::new);
        assertThat(campos).containsExactlyInAnyOrder(
                "id", "marca", "modelo", "carroceria", "preco", "autonomiaKm",
                "capacidadeBateriaKwh", "potenciaCv", "tempoCargaHoras", "fotoUrl", "fornecedor");

        Column marca = campo("marca").getAnnotation(Column.class);
        assertThat(marca).isNotNull();
        assertThat(marca.nullable()).isFalse();

        Column modelo = campo("modelo").getAnnotation(Column.class);
        assertThat(modelo).isNotNull();
        assertThat(modelo.nullable()).isFalse();

        Column carroceria = campo("carroceria").getAnnotation(Column.class);
        assertThat(carroceria).isNotNull();
        assertThat(carroceria.length()).isEqualTo(50);

        Field fornecedor = campo("fornecedor");
        assertThat(fornecedor.getType()).isEqualTo(Fornecedor.class);
        assertThat(fornecedor.getAnnotation(ManyToOne.class)).isNotNull();

        JoinColumn joinColumn = fornecedor.getAnnotation(JoinColumn.class);
        assertThat(joinColumn).isNotNull();
        assertThat(joinColumn.name()).isEqualTo("fornecedor_id");
    }

    private static Field campo(String nome) {
        try {
            return Veiculo.class.getDeclaredField(nome);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("campo '" + nome + "' não existe em Veiculo", e);
        }
    }

}
