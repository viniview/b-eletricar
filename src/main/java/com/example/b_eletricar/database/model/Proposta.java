package com.example.b_eletricar.database.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposta")
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeVisitante;
    private String emailVisitante;
    private String telefoneVisitante;

    private String tipoSolicitacao;
    private LocalDateTime dataSolicitacao = LocalDateTime.now();
    private String status;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeVisitante() { return nomeVisitante; }
    public void setNomeVisitante(String nomeVisitante) { this.nomeVisitante = nomeVisitante; }

    public String getEmailVisitante() { return emailVisitante; }
    public void setEmailVisitante(String emailVisitante) { this.emailVisitante = emailVisitante; }

    public String getTelefoneVisitante() { return telefoneVisitante; }
    public void setTelefoneVisitante(String telefoneVisitante) { this.telefoneVisitante = telefoneVisitante; }

    public String getTipoSolicitacao() { return tipoSolicitacao; }
    public void setTipoSolicitacao(String tipoSolicitacao) { this.tipoSolicitacao = tipoSolicitacao; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
}