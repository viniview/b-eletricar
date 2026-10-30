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

class FornecedorFormValidationTest {

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

    private FornecedorForm formValido() {
        FornecedorForm form = new FornecedorForm();
        form.setNome("Tesla");
        form.setEmailContato("contato@tesla.com");
        form.setSite("https://tesla.com");
        return form;
    }

    private Set<ConstraintViolation<FornecedorForm>> validar(FornecedorForm form) {
        return validator.validate(form);
    }

    private void assertCampoInvalido(FornecedorForm form, String campo) {
        assertThat(validar(form))
                .as("campo '%s' deveria ter violação", campo)
                .anySatisfy(violacao -> assertThat(violacao.getPropertyPath().toString()).isEqualTo(campo));
    }

    private void assertSemViolacao(FornecedorForm form) {
        assertThat(validar(form)).isEmpty();
    }

    @Test
    void nomeVazioOuEmBrancoGeraViolacao() {
        FornecedorForm vazio = formValido();
        vazio.setNome("");

        FornecedorForm emBranco = formValido();
        emBranco.setNome("   ");

        FornecedorForm nulo = formValido();
        nulo.setNome(null);

        assertCampoInvalido(vazio, "nome");
        assertCampoInvalido(emBranco, "nome");
        assertCampoInvalido(nulo, "nome");
    }

    @Test
    void nomeAcimaDe100CaracteresGeraViolacao() {
        FornecedorForm longo = formValido();
        longo.setNome("a".repeat(101));
        assertCampoInvalido(longo, "nome");

        FornecedorForm noLimite = formValido();
        noLimite.setNome("a".repeat(100));
        assertSemViolacao(noLimite);
    }

    @Test
    void emailInvalidoGeraViolacaoEValidoOuVazioNao() {
        FornecedorForm invalido = formValido();
        invalido.setEmailContato("abc");
        assertCampoInvalido(invalido, "emailContato");

        FornecedorForm vazio = formValido();
        vazio.setEmailContato("");
        assertSemViolacao(vazio);

        FornecedorForm nulo = formValido();
        nulo.setEmailContato(null);
        assertSemViolacao(nulo);

        FornecedorForm valido = formValido();
        valido.setEmailContato("a@b.com");
        assertSemViolacao(valido);
    }

    @Test
    void siteSemHttpGeraViolacaoEUrlValidaOuVaziaNao() {
        FornecedorForm invalido = formValido();
        invalido.setSite("ftp://x.com");
        assertCampoInvalido(invalido, "site");

        FornecedorForm valido = formValido();
        valido.setSite("https://exemplo.com");
        assertSemViolacao(valido);

        FornecedorForm vazio = formValido();
        vazio.setSite("");
        assertSemViolacao(vazio);

        FornecedorForm nulo = formValido();
        nulo.setSite(null);
        assertSemViolacao(nulo);
    }

    @Test
    void formCompletoValidoNaoTemViolacoes() {
        assertSemViolacao(formValido());
    }

}
