package com.example.b_eletricar.database.model;

import jakarta.persistence.*;

@Entity
@Table(name = "veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(length = 50)
    private String carroceria;

    private Double preco;

    private Integer quantidadeEstoque;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "especificacao_id", referencedColumnName = "id")
    private Especificacao especificacao;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getCarroceria() { return carroceria; }
    public void setCarroceria(String carroceria) { this.carroceria = carroceria; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public Especificacao getEspecificacao() { return especificacao; }
    public void setEspecificacao(Especificacao especificacao) { this.especificacao = especificacao; }

    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }
}