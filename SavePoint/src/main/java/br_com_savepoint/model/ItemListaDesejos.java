package br_com_savepoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

/** Jogo adicionado à lista de desejos, com preço de alerta. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_item_lista_desejos")
public class ItemListaDesejos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double precoAlerta;
    private boolean notificarOferta;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "lista_desejos_id", nullable = false)
    private ListaDesejos listaDesejos;

    @ManyToOne
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;
}
