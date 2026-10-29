package com.example.b_eletricar.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.b_eletricar.database.model.enums.Roles;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UsuarioAdminFormValidationTest {

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

    private UsuarioAdminForm formValido() {
        UsuarioAdminForm form = new UsuarioAdminForm();
        form.setNome("João da Silva");
        form.setRole(Roles.USER);
        return form;
    }

    private Set<ConstraintViolation<UsuarioAdminForm>> validar(UsuarioAdminForm form) {
        return validator.validate(form);
    }

    private void assertCampoInvalido(UsuarioAdminForm form, String campo) {
        assertThat(validar(form))
                .as("campo '%s' deveria ter violação", campo)
                .anySatisfy(violacao -> assertThat(violacao.getPropertyPath().toString()).isEqualTo(campo));
    }

    private void assertSemViolacao(UsuarioAdminForm form) {
        assertThat(validar(form)).isEmpty();
    }

    @Test
    void formValidoNaoTemViolacoes() {
        assertSemViolacao(formValido());
    }

    @Test
    void nomeVazioOuEmBrancoGeraViolacao() {
        UsuarioAdminForm vazio = formValido();
        vazio.setNome("");

        UsuarioAdminForm emBranco = formValido();
        emBranco.setNome("   ");

        UsuarioAdminForm nulo = formValido();
        nulo.setNome(null);

        assertCampoInvalido(vazio, "nome");
        assertCampoInvalido(emBranco, "nome");
        assertCampoInvalido(nulo, "nome");
    }

    @Test
    void nomeAcimaDe100CaracteresGeraViolacao() {
        UsuarioAdminForm longo = formValido();
        longo.setNome("a".repeat(101));
        assertCampoInvalido(longo, "nome");

        UsuarioAdminForm noLimite = formValido();
        noLimite.setNome("a".repeat(100));
        assertSemViolacao(noLimite);
    }

    @Test
    void roleNuloGeraViolacao() {
        UsuarioAdminForm nulo = formValido();
        nulo.setRole(null);
        assertCampoInvalido(nulo, "role");
    }

}
