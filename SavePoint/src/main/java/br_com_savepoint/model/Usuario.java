package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String senha;
    private String telefone;
    private LocalDateTime dataCadastro = LocalDateTime.now();
    private LocalDateTime ultimoAcesso;
    private Boolean ativo = true;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private ListaDesejos listaDesejos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<AvaliacaoUsuario> avaliacoes;

    public Usuario() {
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    public Usuario(Long id, String nome, String email, String senha, String telefone, LocalDateTime dataCadastro, LocalDateTime ultimoAcesso, Boolean ativo, ListaDesejos listaDesejos, List<AvaliacaoUsuario> avaliacoes) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.dataCadastro = dataCadastro;
        this.ultimoAcesso = ultimoAcesso;
        this.ativo = ativo;
        this.listaDesejos = listaDesejos;
        this.avaliacoes = avaliacoes;
    }

    public void marcarAcesso() {
        this.ultimoAcesso = LocalDateTime.now();
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", ativo=" + ativo +
                '}';
    }
}