package io.github.cursodsousa.mscartoes.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Enumerated(EnumType.STRING)
    private BandeiraCartao bandeira;
    private BigDecimal renda;
    private BigDecimal limiteBasico;

    public Cartao(){

    }

    public Cartao(Long id, String nome, BandeiraCartao bandeira, BigDecimal renda, BigDecimal limiteBasico) {
        this.id = id;
        this.nome = nome;
        this.bandeira = bandeira;
        this.renda = renda;
        this.limiteBasico = limiteBasico;
    }

    public Cartao(String nome, BandeiraCartao bandeira, BigDecimal renda, BigDecimal limite) {
        this.nome = nome;
        this.bandeira = bandeira;
        this.renda = renda;
        this.limiteBasico = limite;
    }
}
