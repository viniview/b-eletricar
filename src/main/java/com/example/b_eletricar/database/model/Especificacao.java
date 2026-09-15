package com.example.b_eletricar.database.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tespecificacao")
public class Especificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer autonomiaKm;
    private Double capacidadeBateriaKwh;
    private Integer potenciaCv;
    private Double tempoCargaHoras;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getAutonomiaKm() { return autonomiaKm; }
    public void setAutonomiaKm(Integer autonomiaKm) { this.autonomiaKm = autonomiaKm; }

    public Double getCapacidadeBateriaKwh() { return capacidadeBateriaKwh; }
    public void setCapacidadeBateriaKwh(Double capacidadeBateriaKwh) { this.capacidadeBateriaKwh = capacidadeBateriaKwh; }

    public Integer getPotenciaCv() { return potenciaCv; }
    public void setPotenciaCv(Integer potenciaCv) { this.potenciaCv = potenciaCv; }

    public Double getTempoCargaHoras() { return tempoCargaHoras; }
    public void setTempoCargaHoras(Double tempoCargaHoras) { this.tempoCargaHoras = tempoCargaHoras; }
}