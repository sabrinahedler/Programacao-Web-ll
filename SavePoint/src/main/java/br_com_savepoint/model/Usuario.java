package br_com_savepoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Usuário cadastrado no sistema. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "email é obrigatório")
    @Email(message = "email inválido")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, message = "senha deve ter no mínimo 6 caracteres")
    private String senha;

    private String telefone;
    private LocalDateTime dataCadastro = LocalDateTime.now();
    private LocalDateTime ultimoAcesso;
    private Boolean ativo = true;

    @JsonIgnore
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private ListaDesejos listaDesejos;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<AvaliacaoUsuario> avaliacoes = new ArrayList<>();

    /** Marca o usuário como ativo. */
    public void ativar() {
        this.ativo = true;
    }

    /** Marca o usuário como inativo. */
    public void desativar() {
        this.ativo = false;
    }
}
