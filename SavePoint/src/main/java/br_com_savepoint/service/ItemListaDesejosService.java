package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.exception.RegraNegocioException;
import br_com_savepoint.model.ItemListaDesejos;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.ListaDesejos;
import br_com_savepoint.repository.ItemListaDesejosRepository;
import br_com_savepoint.util.Validacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras de negócio dos itens da lista de desejos. */
@Service
@Transactional
public class ItemListaDesejosService {

    private final ItemListaDesejosRepository itemRepository;
    private final ListaDesejosService listaDesejosService;
    private final JogoService jogoService;

    public ItemListaDesejosService(ItemListaDesejosRepository itemRepository,
                                   ListaDesejosService listaDesejosService,
                                   JogoService jogoService) {
        this.itemRepository = itemRepository;
        this.listaDesejosService = listaDesejosService;
        this.jogoService = jogoService;
    }

    /** Adiciona um jogo à lista de desejos do usuário, evitando repetições. */
    public ItemListaDesejos adicionarItem(Long usuarioId, ItemListaDesejos item) {
        ListaDesejos lista = listaDesejosService.buscarPorUsuario(usuarioId);
        Validacao.exigirPreenchido(item.getJogo(), "jogo");
        Validacao.exigirPreenchido(item.getJogo().getId(), "jogo.id");
        Validacao.exigirNaoNegativo(item.getPrecoAlerta(), "precoAlerta");
        Jogo jogo = jogoService.buscarPorId(item.getJogo().getId());

        if (itemRepository.existsByListaDesejosIdAndJogoId(lista.getId(), jogo.getId())) {
            throw new RegraNegocioException("O jogo já está na lista de desejos do usuário.");
        }

        item.setId(null);
        item.setListaDesejos(lista);
        item.setJogo(jogo);
        return itemRepository.save(item);
    }

    /** Busca um item da lista de desejos do usuário ou falha se ele não existir. */
    public ItemListaDesejos buscarItem(Long usuarioId, Long itemId) {
        ListaDesejos lista = listaDesejosService.buscarPorUsuario(usuarioId);
        return itemRepository.findById(itemId)
                .filter(item -> item.getListaDesejos().getId().equals(lista.getId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Item " + itemId + " não encontrado na lista do usuário " + usuarioId + "."));
    }

    /** Atualiza o preço de alerta e a notificação de um item. */
    public ItemListaDesejos atualizarItem(Long usuarioId, Long itemId, ItemListaDesejos dados) {
        ItemListaDesejos item = buscarItem(usuarioId, itemId);
        Validacao.exigirNaoNegativo(dados.getPrecoAlerta(), "precoAlerta");

        item.setPrecoAlerta(dados.getPrecoAlerta());
        item.setNotificarOferta(dados.isNotificarOferta());
        return itemRepository.save(item);
    }
}
