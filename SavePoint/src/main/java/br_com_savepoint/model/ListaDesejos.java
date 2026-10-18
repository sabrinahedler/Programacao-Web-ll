package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_lista_desejos")
public class ListaDesejos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "listaDesejos", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemListaDesejos> jogos = new ArrayList<>();

    public ListaDesejos() {
    }

    public ListaDesejos(Long id, Usuario usuario, List<ItemListaDesejos> jogos) {
        this.id = id;
        this.usuario = usuario;
        this.jogos = jogos;
    }

    public boolean adicionarJogo(ItemListaDesejos item) {
        item.setListaDesejos(this);
        return this.jogos.add(item);
    }

    public boolean removerJogo(Jogo jogo) {
        return this.jogos.removeIf(item -> item.getJogo().getId().equals(jogo.getId()));
    }
}