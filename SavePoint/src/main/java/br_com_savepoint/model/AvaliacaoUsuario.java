package br_com_savepoint.model;

import java.time.LocalDateTime;

public class AvaliacaoUsuario {

    private Long id;
    private int nota;
    private String textoAvaliacao;
    private LocalDateTime dataPublicacao;
    private int curtidas;
    private int horasJogadas;
    private int utilVoto;
    private Usuario usuario;
    private Jogo jogo;
    
    public AvaliacaoUsuario() {
        this.dataPublicacao = LocalDateTime.now();
        this.curtidas = 0;
        this.utilVoto = 0;
        this.horasJogadas = 0;
    }

    public AvaliacaoUsuario(Long id, int nota, String textoAvaliacao, Usuario usuario, Jogo jogo) {
        this.id = id;
        this.nota = nota;
        this.textoAvaliacao = textoAvaliacao;
        this.dataPublicacao = LocalDateTime.now();
        this.curtidas = 0;
        this.utilVoto = 0;
        this.horasJogadas = 0;
        this.usuario = usuario;
        this.jogo = jogo;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getTextoAvaliacao() {
        return textoAvaliacao;
    }

    public void setTextoAvaliacao(String textoAvaliacao) {
        this.textoAvaliacao = textoAvaliacao;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public int getCurtidas() {
        return curtidas;
    }

    public void setCurtidas(int curtidas) {
        this.curtidas = curtidas;
    }

    public int getHorasJogadas() {
        return horasJogadas;
    }

    public void setHorasJogadas(int horasJogadas) {
        this.horasJogadas = horasJogadas;
    }

    public int getUtilVoto() {
        return utilVoto;
    }

    public void setUtilVoto(int utilVoto) {
        this.utilVoto = utilVoto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    //  Métodos
    public void curtir() {
        this.curtidas++;
    }

    public void marcarComoUtil() {
        this.utilVoto++;
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
                ", utilVoto=" + utilVoto +
                ", usuario=" + (usuario != null ? usuario.getId() : null) +
                ", jogo=" + (jogo != null ? jogo.getId() : null) +
                '}';
    }
}
