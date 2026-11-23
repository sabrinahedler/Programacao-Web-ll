package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Data
@Entity
@Table(name = "tb_oferta_jogo")
public class OfertaJogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double precoOriginal;
    private double precoAtual;
    private Float percentualDesconto;

    @ManyToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    @ManyToOne
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Historico> historico;

    public OfertaJogo () {
    }

    public OfertaJogo(Long id, double precoOriginal, double precoAtual, Float percentualDesconto, Jogo jogo, Loja loja, List<Historico> historico) {
        this.id = id;
        this.precoOriginal = precoOriginal;
        this.precoAtual = precoAtual;
        this.percentualDesconto = 0.0F;
        this.jogo = jogo;
        this.loja = loja;
        this.historico = historico;
    }
}
