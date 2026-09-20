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

    public boolean adicionarJogo(Jogo jogo) {
        ItemListaDesejos novoItem = new ItemListaDesejos();
        novoItem.setJogo(jogo);
        novoItem.setListaDesejos(this);
        novoItem.setNotificarOferta(true); 
        novoItem.setPrecoAlerta(0.0); 
        
        return this.jogos.add(novoItem);
    }

    public boolean removerJogo(Jogo jogo) {
        return this.jogos.removeIf(item -> item.getJogo().getId().equals(jogo.getId()));
    }

    public void notificarPromocao() {
        // a lógica para envio de e-mail/notificação será implementada depois na camada de Service
    }
}