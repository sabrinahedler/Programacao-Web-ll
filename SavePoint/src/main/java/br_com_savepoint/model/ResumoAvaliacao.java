package br_com_savepoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Resumo consolidado das avaliações de um jogo. */
@Getter
@Setter
@NoArgsConstructor
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
    private List<String> principaisElogios = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "tb_resumo_criticas", joinColumns = @JoinColumn(name = "resumo_id"))
    @Column(name = "critica")
    private List<String> principaisCriticas = new ArrayList<>();

    @Column(name = "resumo_gerado_ia", columnDefinition = "TEXT")
    private String resumoGeradoIA;

    private LocalDate dataGeracao;

    @JsonIgnore
    @OneToOne(mappedBy = "resumoAvaliacao")
    private Jogo jogo;
}
