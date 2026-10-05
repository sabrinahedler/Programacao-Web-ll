package br_com_savepoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Avaliação de um usuário sobre um jogo. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_avaliacao_usuario")
public class AvaliacaoUsuario {

    private static final int NOTA_MINIMA_RECOMENDACAO = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int nota;

    @Column(columnDefinition = "TEXT")
    private String textoAvaliacao;

    private LocalDateTime dataPublicacao = LocalDateTime.now();
    private int curtidas;
    private int horasJogadas;
    private boolean indiceRecomendacao;

    @JsonIgnoreProperties({"email", "telefone", "dataCadastro", "ultimoAcesso", "ativo"})
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    /** Incrementa o número de curtidas da avaliação. */
    public void curtir() {
        this.curtidas++;
    }

    /** Indica se a nota da avaliação recomenda o jogo. */
    @JsonIgnore
    public boolean isRecomendado() {
        return nota >= NOTA_MINIMA_RECOMENDACAO;
    }
}
