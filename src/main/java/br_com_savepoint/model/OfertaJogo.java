package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "tb_ofertaJogo")
public class OfertaJogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double precoOriginal;
    private double precoAtual;
    private float percentualDesconto;

    public OfertaJogo () {

    }

    public OfertaJogo (Long id, double precoOriginal, double precoAtual, float percentualDesconto) {
        this.id = id;
        this.precoOriginal = precoOriginal;
        this.precoAtual = precoAtual;
        this.percentualDesconto = percentualDesconto;
    }

    @ManyToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    @ManyToOne
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL)
    private List<Historico> historico;
}
