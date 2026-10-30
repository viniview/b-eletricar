package com.example.b_eletricar.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class VeiculoFormValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void criarValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void fecharValidator() {
        factory.close();
    }

    private VeiculoForm formValido() {
        VeiculoForm form = new VeiculoForm();
        form.setMarca("Tesla");
        form.setModelo("Model 3");
        form.setCarroceria("Sedan");
        form.setPreco(250000.0);
        form.setAutonomiaKm(500);
        form.setCapacidadeBateriaKwh(75.0);
        form.setPotenciaCv(300);
        form.setTempoCargaHoras(8.5);
        form.setFornecedorId(1L);
        return form;
    }

    private Set<ConstraintViolation<VeiculoForm>> validar(VeiculoForm form) {
        return validator.validate(form);
    }

    private void assertCampoInvalido(VeiculoForm form, String campo) {
        assertThat(validar(form))
                .as("campo '%s' deveria ter violação", campo)
                .anySatisfy(violacao -> assertThat(violacao.getPropertyPath().toString()).isEqualTo(campo));
    }

    private void assertMensagem(VeiculoForm form, String campo, String mensagem) {
        assertThat(validar(form))
                .as("campo '%s' deveria ter a mensagem '%s'", campo, mensagem)
                .anySatisfy(violacao -> {
                    assertThat(violacao.getPropertyPath().toString()).isEqualTo(campo);
                    assertThat(violacao.getMessage()).isEqualTo(mensagem);
                });
    }

    private void assertSemViolacao(VeiculoForm form) {
        assertThat(validar(form)).isEmpty();
    }

    @Test
    void formCompletoValidoNaoTemViolacoes() {
        assertSemViolacao(formValido());
    }

    @Test
    void marcaVaziaOuEmBrancoGeraViolacao() {
        VeiculoForm vazio = formValido();
        vazio.setMarca("");

        VeiculoForm emBranco = formValido();
        emBranco.setMarca("   ");

        VeiculoForm nulo = formValido();
        nulo.setMarca(null);

        assertCampoInvalido(vazio, "marca");
        assertCampoInvalido(emBranco, "marca");
        assertCampoInvalido(nulo, "marca");
    }

    @Test
    void modeloVazioOuEmBrancoGeraViolacao() {
        VeiculoForm vazio = formValido();
        vazio.setModelo("");

        VeiculoForm emBranco = formValido();
        emBranco.setModelo("   ");

        VeiculoForm nulo = formValido();
        nulo.setModelo(null);

        assertCampoInvalido(vazio, "modelo");
        assertCampoInvalido(emBranco, "modelo");
        assertCampoInvalido(nulo, "modelo");
    }

    @Test
    void precoNuloZeroOuNegativoGeraViolacao() {
        VeiculoForm nulo = formValido();
        nulo.setPreco(null);

        VeiculoForm zero = formValido();
        zero.setPreco(0.0);

        VeiculoForm negativo = formValido();
        negativo.setPreco(-10.0);

        assertCampoInvalido(nulo, "preco");
        assertMensagem(zero, "preco", "Informe um preço maior que zero");
        assertMensagem(negativo, "preco", "Informe um preço maior que zero");
    }

    @Test
    void autonomiaForaDoIntervaloGeraViolacao() {
        VeiculoForm nulo = formValido();
        nulo.setAutonomiaKm(null);

        VeiculoForm zero = formValido();
        zero.setAutonomiaKm(0);

        VeiculoForm acima = formValido();
        acima.setAutonomiaKm(2001);

        assertCampoInvalido(nulo, "autonomiaKm");
        assertCampoInvalido(zero, "autonomiaKm");
        assertCampoInvalido(acima, "autonomiaKm");
    }

    @Test
    void capacidadeBateriaZeroOuAcimaDe300GeraViolacao() {
        VeiculoForm nulo = formValido();
        nulo.setCapacidadeBateriaKwh(null);

        VeiculoForm zero = formValido();
        zero.setCapacidadeBateriaKwh(0.0);

        VeiculoForm acima = formValido();
        acima.setCapacidadeBateriaKwh(301.0);

        assertCampoInvalido(nulo, "capacidadeBateriaKwh");
        assertCampoInvalido(zero, "capacidadeBateriaKwh");
        assertCampoInvalido(acima, "capacidadeBateriaKwh");
    }

    @Test
    void fornecedorIdNuloGeraViolacao() {
        VeiculoForm nulo = formValido();
        nulo.setFornecedorId(null);

        assertMensagem(nulo, "fornecedorId", "Selecione um fornecedor");
    }

    @Test
    void potenciaCvOpcionalMasSeInformadaDeveSerPositiva() {
        VeiculoForm nulo = formValido();
        nulo.setPotenciaCv(null);
        assertSemViolacao(nulo);

        VeiculoForm zero = formValido();
        zero.setPotenciaCv(0);
        assertCampoInvalido(zero, "potenciaCv");

        VeiculoForm negativa = formValido();
        negativa.setPotenciaCv(-10);
        assertCampoInvalido(negativa, "potenciaCv");
    }

    @Test
    void tempoCargaHorasOpcionalMasSeInformadoDeveSerPositivo() {
        VeiculoForm nulo = formValido();
        nulo.setTempoCargaHoras(null);
        assertSemViolacao(nulo);

        VeiculoForm zero = formValido();
        zero.setTempoCargaHoras(0.0);
        assertCampoInvalido(zero, "tempoCargaHoras");

        VeiculoForm negativo = formValido();
        negativo.setTempoCargaHoras(-1.0);
        assertCampoInvalido(negativo, "tempoCargaHoras");
    }

}
