package br_com_savepoint.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_avaliacao_usuario")
public class AvaliacaoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int nota;

    @Column(columnDefinition = "TEXT")
    private String textoAvaliacao;

    private LocalDateTime dataPublicacao = LocalDateTime.now();
    private int curtidas = 0;
    private int horasJogadas = 0;
    private boolean indiceRecomendacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;
    
    public AvaliacaoUsuario() {
        this.dataPublicacao = LocalDateTime.now();
        this.curtidas = 0;
        this.indiceRecomendacao = false;
        this.horasJogadas = 0;
    }

    public AvaliacaoUsuario(Long id, int nota, String textoAvaliacao, LocalDateTime dataPublicacao, int curtidas, int horasJogadas, boolean indiceRecomendacao, Usuario usuario, Jogo jogo) {
        this.id = id;
        this.nota = nota;
        this.textoAvaliacao = textoAvaliacao;
        this.dataPublicacao = dataPublicacao;
        this.curtidas = curtidas;
        this.horasJogadas = horasJogadas;
        this.indiceRecomendacao = indiceRecomendacao;
        this.usuario = usuario;
        this.jogo = jogo;
    }

    public String avaliar(Usuario usuario, Jogo jogo) {
        this.usuario = usuario;
        this.jogo = jogo;
        this.dataPublicacao = LocalDateTime.now();
        this.indiceRecomendacao = this.isRecomendado();
        return "Avaliação registrada.";
    }

    public void curtir() {
        this.curtidas++;
    }

    public boolean isRecomendado() {
        return this.nota >= 4;
    }

    @Override
    public String toString() {
        return "AvaliacaoUsuario{" +
                "id=" + id +
                ", nota=" + nota +
                ", textoAvaliacao='" + textoAvaliacao + '\'' +
                ", dataPublicacao=" + dataPublicacao +
                ", curtidas=" + curtidas +
                ", horasJogadas=" + horasJogadas +
                ", indiceRecomendacao=" + indiceRecomendacao +
                ", usuario=" + (usuario != null ? usuario.getId() : null) +
                ", jogo=" + (jogo != null ? jogo.getId() : null) +
                '}';
    }
}
