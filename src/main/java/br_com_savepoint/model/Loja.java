package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "tb_loja")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String urlLoja;
    private String urlLogo;

    public Loja () {

    }

    public Loja (Long id, String nome, String urlLoja, String urlLogo) {
        this.id = id;
        this.nome = nome;
        this.urlLoja = urlLoja;
        this.urlLogo = urlLogo;
    }

    @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL)
    private List<OfertaJogo> ofertas;
}
