package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "tb_resumo_avaliacao")
public class ResumoAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double notaMedia;
    private int totalAvaliacoes;
    private double percentualRecomendacao;

    @ElementCollection
    @CollectionTable(name = "tb_resumo_elogios", joinColumns = @JoinColumn(name = "resumo_id"))
    @Column(name = "elogio")
    private String[] principaisElogios;

    @ElementCollection
    @CollectionTable(name = "tb_resumo_criticas", joinColumns = @JoinColumn(name = "resumo_id"))
    @Column(name = "critica")
    private String[] principaisCriticas;

    @Column(columnDefinition = "TEXT")
    private String resumoGeradoIA;

    private LocalDate dataGeracao;

    @OneToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    public ResumoAvaliacao() {
    }

    public ResumoAvaliacao(Long id, double notaMedia, int totalAvaliacoes, double percentualRecomendacao, String[] principaisElogios, String[] principaisCriticas, String resumoGeradoIA, LocalDate dataGeracao, Jogo jogo) {
        this.id = id;
        this.notaMedia = notaMedia;
        this.totalAvaliacoes = totalAvaliacoes;
        this.percentualRecomendacao = percentualRecomendacao;
        this.principaisElogios = principaisElogios;
        this.principaisCriticas = principaisCriticas;
        this.resumoGeradoIA = resumoGeradoIA;
        this.dataGeracao = dataGeracao;
        this.jogo = jogo;
    }
}