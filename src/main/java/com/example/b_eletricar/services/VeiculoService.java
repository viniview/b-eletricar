package com.example.b_eletricar.services;

import java.util.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.b_eletricar.database.model.Veiculo;
import com.example.b_eletricar.database.repository.VeiculoRepository;

import com.example.b_eletricar.database.model.Fornecedor;

@Service
public class VeiculoService {

    public final VeiculoRepository veiculoRepository;


    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    // Busca de um veículo pelo ID
    public Optional<Veiculo> buscarVeiculoPorId(Long id) {
        return veiculoRepository.findById(id);
    }

    // Listagens de veículos por:

    public List<Veiculo> filtrarVeiculos(
            String marca, // Marca
            String modelo, // Modelo
            String carroceria, // Carroceria
            Double precoMin,
            Double precoMax, // Preço
            Integer autonomiaKmMin, 
            Integer autonomiaKmMax,// Autonomia
            Double capacidadeBateriaKwhMin, 
            Double capacidadeBateriaKwhMax, // Capacidade da bateria
            Integer potenciaCvMin,
            Integer potenciaCvMax, // Potência
            Double tempoCargaHorasMin,
            Double tempoCargaHorasMax, // Tempo de carga
            String palavraChave) { // Palavra chave

        Specification<Veiculo> spec = (root, query, cb) -> cb.conjunction();
        

        if (marca != null && !marca.trim().isEmpty()) {
            String marcaNormalizada = marca.trim().toLowerCase();
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("marca")), "%" + marcaNormalizada + "%"));
        } 
        if (modelo != null && !modelo.trim().isEmpty()) {
            String modeloNormalizado = modelo.trim().toLowerCase();
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("modelo")), "%" + modeloNormalizado + "%"));
        } 
        if (carroceria != null && !carroceria.trim().isEmpty()) {
            String carroceriaNormalizada = carroceria.trim().toLowerCase();
            spec = spec.and((root, query, cb) -> 
                cb.equal(cb.lower(root.get("carroceria")), carroceriaNormalizada));
        } 
        if (precoMin != null && precoMax != null    && precoMin > precoMax) {
            throw new IllegalArgumentException("O preço mínimo não pode ser maior que o máximo.");  }
        if (precoMin != null && precoMax != null) {
            spec = spec.and((root, query, cb) ->
                cb.between(root.get("preco"), precoMin, precoMax)
            );
        } else if (precoMin != null) {
            spec = spec.and((root, query, cb) ->
                cb.ge(root.get("preco"), precoMin)
            );
        } else if (precoMax != null) {
            spec = spec.and((root, query, cb) ->
                cb.le(root.get("preco"), precoMax)
            );
        } 
        if (autonomiaKmMin != null && autonomiaKmMax != null && autonomiaKmMin > autonomiaKmMax) { 
            throw new IllegalArgumentException ("A autonomia mínima não pode ser maior que a máxima."); } 
            if (autonomiaKmMin != null && autonomiaKmMax != null) {
                spec = spec.and((root, query, cb) ->
                    cb.between(root.get("autonomiaKm"),
                            autonomiaKmMin, autonomiaKmMax));
            } else if (autonomiaKmMin != null) {
                spec = spec.and((root, query, cb) ->
                    cb.ge(root.get("autonomiaKm"), autonomiaKmMin));
            } else if (autonomiaKmMax != null) {
                spec = spec.and((root, query, cb) ->
                    cb.le(root.get("autonomiaKm"), autonomiaKmMax));
            }
        if (capacidadeBateriaKwhMin != null && capacidadeBateriaKwhMax != null && capacidadeBateriaKwhMin > capacidadeBateriaKwhMax) { 
            throw new IllegalArgumentException ("A capacidade de bateria mínima não pode ser maior que a máxima."); }
            if (capacidadeBateriaKwhMin != null && capacidadeBateriaKwhMax != null) {
                spec = spec.and((root, query, cb) ->
                    cb.between(root.get("capacidadeBateriaKwh"), capacidadeBateriaKwhMin, capacidadeBateriaKwhMax));
            } else if (capacidadeBateriaKwhMin != null) {
                spec = spec.and((root, query, cb) ->
                    cb.ge(root.get("capacidadeBateriaKwh"), capacidadeBateriaKwhMin));
            }   else if (capacidadeBateriaKwhMax != null) {
                spec = spec.and((root, query, cb) ->
                    cb.le(root.get("capacidadeBateriaKwh"), capacidadeBateriaKwhMax));
            }
        if (potenciaCvMin != null && potenciaCvMax != null&& potenciaCvMin > potenciaCvMax) 
            { throw new IllegalArgumentException ("A potência mínima não pode ser maior que a máxima."); } 
            if (potenciaCvMin != null && potenciaCvMax != null) {
                spec = spec.and((root, query, cb) ->
                    cb.between(root.get("potenciaCv"), potenciaCvMin, potenciaCvMax));
            } else if (potenciaCvMin != null) {
                spec = spec.and((root, query, cb) ->
                    cb.ge(root.get("potenciaCv"), potenciaCvMin));
            }   else if (potenciaCvMax != null) {
                spec = spec.and((root, query, cb) ->
                    cb.le(root.get("potenciaCv"), potenciaCvMax));
            }
        if (tempoCargaHorasMin != null && tempoCargaHorasMax != null && tempoCargaHorasMin > tempoCargaHorasMax) 
            { throw new IllegalArgumentException ("O tempo de carga mínimo não pode ser maior que o máximo."); } 
            if (tempoCargaHorasMin != null && tempoCargaHorasMax != null) {
                spec = spec.and((root, query, cb) ->
                    cb.between(root.get("tempoCargaHoras"), tempoCargaHorasMin, tempoCargaHorasMax));
            } else if (tempoCargaHorasMin != null) {
                spec = spec.and((root, query, cb) ->
                    cb.ge(root.get("tempoCargaHoras"), tempoCargaHorasMin));
            }   else if (tempoCargaHorasMax != null) {
                spec = spec.and((root, query, cb) ->
                    cb.le(root.get("tempoCargaHoras"), tempoCargaHorasMax));
            }
        if (palavraChave != null && !palavraChave.isBlank()) {
            String valor = "%" + palavraChave.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("marca")), valor),
                    cb.like(cb.lower(root.get("modelo")), valor),
                    cb.like(cb.lower(root.get("carroceria")), valor)
                )
            );
        }
        return veiculoRepository.findAll(spec);
    }

    // Redirecionar para dados do fornecedor de um veículo
    public Optional<Fornecedor> buscarFornecedorPorVeiculoId(Long veiculoId) {
        return veiculoRepository.findById(veiculoId)
            .map(veiculo -> veiculo.getFornecedor());
    }

}