package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_usuario")
@JsonIgnoreProperties(value = {"password", "username", "authorities", "accountNonExpired", "accountNonLocked",
        "credentialsNonExpired", "enabled", "hibernateLazyInitializer", "handler"})
public class Usuario implements UserDetails {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoles role = UserRoles.USER;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String senha;
    private String telefone;
    private LocalDateTime dataCadastro = LocalDateTime.now();
    private LocalDateTime ultimoAcesso;
    private Boolean ativo = true;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ListaDesejos listaDesejos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AvaliacaoUsuario> avaliacoes;

    public Usuario() {
        this.role = UserRoles.USER;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    public Usuario(String nome, String email, String senha, UserRoles role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    public Usuario(Long id, UserRoles role, String nome, String email, String senha, String telefone, LocalDateTime dataCadastro, LocalDateTime ultimoAcesso, Boolean ativo, ListaDesejos listaDesejos, List<AvaliacaoUsuario> avaliacoes) {
        this.id = id;
        this.role = role;
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
                ", role='" + role + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", ultimoAcesso=" + ultimoAcesso +
                ", ativo=" + ativo +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRoles.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo != null && this.ativo;
    }
}