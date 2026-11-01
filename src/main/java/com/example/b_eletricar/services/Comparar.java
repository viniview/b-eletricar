package com.example.b_eletricar.services;

import com.example.b_eletricar.database.model.Veiculo;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@Service
public class Comparar {

    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");

    public List<ResultadoAtributo> comparar(List<Veiculo> veiculos) {
        return List.of(
                compararAtributo(veiculos, "Preço", "Menor valor",
                        Veiculo::getPreco, Comparar::formatarMoeda, true),
                compararAtributo(veiculos, "Autonomia", "Maior distância por carga",
                        veiculo -> veiculo.getAutonomiaKm() == null
                                ? null : veiculo.getAutonomiaKm().doubleValue(),
                        valor -> formatarNumero(valor, 0) + " km", false),
                compararAtributo(veiculos, "Bateria", "Maior capacidade",
                        Veiculo::getCapacidadeBateriaKwh,
                        valor -> formatarNumero(valor, 1) + " kWh", false),
                compararAtributo(veiculos, "Potência", "Maior potência",
                        veiculo -> veiculo.getPotenciaCv() == null
                                ? null : veiculo.getPotenciaCv().doubleValue(),
                        valor -> formatarNumero(valor, 0) + " cv", false),
                compararAtributo(veiculos, "Tempo de recarga", "Menor tempo",
                        Veiculo::getTempoCargaHoras,
                        valor -> formatarNumero(valor, 1) + " h", true)
        );
    }

    public List<ResultadoAtributo> compararComAvaliacoes(
            List<Veiculo> veiculos, Map<Long, Double> mediasAvaliacoes) {
        List<ResultadoAtributo> resultados = new ArrayList<>(comparar(veiculos));
        resultados.add(compararAtributo(veiculos, "Avaliação", "Maior nota média",
                veiculo -> mediasAvaliacoes.get(veiculo.getId()),
                valor -> formatarNumero(valor, 1) + " / 5", false));
        return List.copyOf(resultados);
    }

    private ResultadoAtributo compararAtributo(
            List<Veiculo> veiculos,
            String nome,
            String observacao,
            Function<Veiculo, Double> obterValor,
            Function<Double, String> formatarValor,
            boolean menorMelhor) {
        List<Veiculo> comValor = veiculos.stream()
                .filter(veiculo -> obterValor.apply(veiculo) != null)
                .toList();

        List<ValorVeiculo> valores = veiculos.stream()
                .map(veiculo -> {
                    Double valor = obterValor.apply(veiculo);
                    return new ValorVeiculo(
                            nomeCompleto(veiculo),
                            valor == null ? "Sem dados" : formatarValor.apply(valor));
                })
                .toList();

        if (comValor.isEmpty()) {
            return new ResultadoAtributo(nome, observacao, valores, List.of(), List.of());
        }

        double melhorValor = comValor.stream()
                .mapToDouble(veiculo -> obterValor.apply(veiculo))
                .reduce(menorMelhor ? Math::min : Math::max)
                .orElseThrow();

        List<String> vencedores = comValor.stream()
                .filter(veiculo -> Double.compare(obterValor.apply(veiculo), melhorValor) == 0)
                .map(Comparar::nomeCompleto)
                .toList();
        List<Long> vencedoresIds = comValor.stream()
                .filter(veiculo -> Double.compare(obterValor.apply(veiculo), melhorValor) == 0)
                .map(Veiculo::getId)
                .toList();

        return new ResultadoAtributo(nome, observacao, valores, vencedores, vencedoresIds);
    }

    private static String nomeCompleto(Veiculo veiculo) {
        return veiculo.getMarca() + " " + veiculo.getModelo();
    }

    private static String formatarMoeda(Double valor) {
        return NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor);
    }

    private static String formatarNumero(Double valor, int casasDecimais) {
        NumberFormat format = NumberFormat.getNumberInstance(LOCALE_BR);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(casasDecimais);
        return format.format(valor);
    }

    public record ValorVeiculo(String veiculo, String valor) {
    }

    public record ResultadoAtributo(
            String nome,
            String observacao,
            List<ValorVeiculo> valores,
            List<String> vencedores,
            List<Long> vencedoresIds) {
    }
}