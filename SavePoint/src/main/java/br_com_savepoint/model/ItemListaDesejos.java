package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_item_lista_desejos")
public class ItemListaDesejos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double precoAlerta;
    private boolean notificarOferta;

    @ManyToOne
    @JoinColumn(name = "lista_desejos_id", nullable = false)
    private ListaDesejos listaDesejos;

    @ManyToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    public ItemListaDesejos() {
    }

    public ItemListaDesejos(Long id, double precoAlerta, boolean notificarOferta, ListaDesejos listaDesejos, Jogo jogo) {
        this.id = id;
        this.precoAlerta = precoAlerta;
        this.notificarOferta = notificarOferta;
        this.listaDesejos = listaDesejos;
        this.jogo = jogo;
    }
}
