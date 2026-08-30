package br_com_savepoint.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "tb_jogo")
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String titulo;
    private String descricao;
    private LocalDate dataLancamento;
    private String imagemCapa;
    private String classificacaoIndicativa;
    private String desenvolvedora;
    private String genero;

    public Jogo() {
    }

    public Jogo(Long id, String titulo, String descricao, LocalDate dataLancamento, String imagemCapa, String classificacaoIndicativa, String desenvolvedora, String genero, List<OfertaJogo> ofertas) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataLancamento = dataLancamento;
        this.imagemCapa = imagemCapa;
        this.classificacaoIndicativa = classificacaoIndicativa;
        this.desenvolvedora = desenvolvedora;
        this.genero = genero;
        this.ofertas = ofertas;
    }

//Sujeito a mudanças conforme demais criações de classes.
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = requisitos_minimos_id)
    private RequisitosMinimos requisitosMinimos;

    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL)
    private List<OfertaJogo> ofertas;

    @OneToMany(mappedBy = "jogo")
    private List<AvaliacaoUsuario> avaliacoes;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resumo_avaliacao_id")
    private ResumoAvaliacao resumoAvaliacao;
}
