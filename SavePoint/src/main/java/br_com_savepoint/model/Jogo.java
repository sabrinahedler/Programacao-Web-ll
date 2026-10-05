package br_com_savepoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Jogo do catálogo, com requisitos mínimos, ofertas e resumo de avaliações. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_jogo")
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private LocalDate dataLancamento;
    private String imagemCapa;
    private String classificacaoIndicativa;
    private String desenvolvedora;
    private String genero;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "requisitos_minimos_id")
    private RequisitosMinimos requisitosMinimos;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resumo_avaliacao_id")
    private ResumoAvaliacao resumoAvaliacao;

    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL)
    private List<OfertaJogo> ofertas = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "jogo")
    private List<AvaliacaoUsuario> avaliacoes = new ArrayList<>();
}
