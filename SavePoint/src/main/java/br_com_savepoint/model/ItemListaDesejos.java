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

    public ItemListaDesejos() {
    }

    // Liga esse item a uma Lista de Desejos específica
    @ManyToOne
    @JoinColumn(name = "lista_desejos_id", nullable = false)
    private ListaDesejos listaDesejos;

    // Liga esse item a um Jogo específico
    @ManyToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;
}
