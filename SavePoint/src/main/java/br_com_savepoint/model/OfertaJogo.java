package br_com_savepoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Oferta de um jogo em uma loja, com preço original, preço atual e histórico. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_oferta_jogo")
public class OfertaJogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double precoOriginal;
    private double precoAtual;
    private float percentualDesconto;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    @ManyToOne
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL)
    private List<Historico> historico = new ArrayList<>();

    /** Atualiza o preço atual e recalcula o percentual de desconto. */
    public void atualizarPrecoAtual(double novoPreco) {
        this.precoAtual = novoPreco;
        this.percentualDesconto = calcularPercentualDesconto();
    }

    /** Calcula o desconto do preço atual em relação ao preço original. */
    public float calcularPercentualDesconto() {
        if (precoOriginal <= 0) {
            return 0;
        }
        double desconto = (1 - precoAtual / precoOriginal) * 100;
        return (float) Math.max(0, desconto);
    }
}
