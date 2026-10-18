package br_com_savepoint.service;

import br_com_savepoint.model.ItemListaDesejos;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.ListaDesejos;
import br_com_savepoint.repository.ItemListaDesejosRepository;
import br_com_savepoint.repository.JogoRepository;
import br_com_savepoint.repository.ListaDesejosRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemListaDesejosService {

    private final ItemListaDesejosRepository itemListaDesejosRepository;
    private final ListaDesejosRepository listaDesejosRepository;
    private final JogoRepository jogoRepository;

    public ItemListaDesejosService(
            ItemListaDesejosRepository itemListaDesejosRepository,
            ListaDesejosRepository listaDesejosRepository,
            JogoRepository jogoRepository) {
        this.itemListaDesejosRepository = itemListaDesejosRepository;
        this.listaDesejosRepository = listaDesejosRepository;
        this.jogoRepository = jogoRepository;
    }

    // POST /usuarios/{id}/lista-desejos/itens
    public ItemListaDesejos adicionarItem(
            Long usuarioId,
            ItemListaDesejos itemDados) {

        Optional<ListaDesejos> listaOptional =
                listaDesejosRepository.findByUsuarioId(usuarioId);

        if (listaOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Lista de desejos não encontrada para o usuário: " + usuarioId);
        }

        if (itemDados.getJogo() == null
                || itemDados.getJogo().getId() == null) {
            throw new IllegalArgumentException("O jogo deve ser informado");
        }

        Long jogoId = itemDados.getJogo().getId();
        Optional<Jogo> jogoOptional = jogoRepository.findById(jogoId);

        if (jogoOptional.isEmpty()) {
            throw new IllegalArgumentException("Jogo não encontrado com o ID: " + jogoId);
        }

        if (itemDados.getPrecoAlerta() < 0) {
            throw new IllegalArgumentException("O preço de alerta não pode ser negativo");
        }

        ListaDesejos lista = listaOptional.get();
        itemDados.setListaDesejos(lista);
        itemDados.setJogo(jogoOptional.get());

        return itemListaDesejosRepository.save(itemDados);
    }

    // GET /usuarios/{id}/lista-desejos/itens/{itemId}
    public Optional<ItemListaDesejos> buscarItem(
            Long usuarioId,
            Long itemId) {

        Optional<ListaDesejos> listaOptional =
                listaDesejosRepository.findByUsuarioId(usuarioId);

        if (listaOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<ItemListaDesejos> itemOptional =
                itemListaDesejosRepository.findById(itemId);

        if (itemOptional.isEmpty()) {
            return Optional.empty();
        }

        ItemListaDesejos item = itemOptional.get();

        if (item.getListaDesejos() == null
                || !item.getListaDesejos().getId().equals(listaOptional.get().getId())) {
            return Optional.empty();
        }

        return Optional.of(item);
    }

    // PUT /usuarios/{id}/lista-desejos/itens/{itemId}
    public Optional<ItemListaDesejos> atualizarItem(
            Long usuarioId,
            Long itemId,
            ItemListaDesejos itemDados) {

        Optional<ItemListaDesejos> itemOptional = buscarItem(usuarioId, itemId);

        if (itemOptional.isEmpty()) {
            return Optional.empty();
        }

        if (itemDados.getPrecoAlerta() < 0) {
            throw new IllegalArgumentException("O preço de alerta não pode ser negativo");
        }

        ItemListaDesejos itemExistente = itemOptional.get();
        itemExistente.setPrecoAlerta(itemDados.getPrecoAlerta());
        itemExistente.setNotificarOferta(itemDados.isNotificarOferta());

        return Optional.of(itemListaDesejosRepository.save(itemExistente));
    }
}
