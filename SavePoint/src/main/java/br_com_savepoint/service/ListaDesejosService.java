package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.model.ListaDesejos;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.ItemListaDesejosRepository;
import br_com_savepoint.repository.ListaDesejosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio da lista de desejos dos usuários. */
@Service
@Transactional
public class ListaDesejosService {

    private final ListaDesejosRepository listaDesejosRepository;
    private final ItemListaDesejosRepository itemRepository;
    private final UsuarioService usuarioService;
    private final JogoService jogoService;

    public ListaDesejosService(ListaDesejosRepository listaDesejosRepository,
                               ItemListaDesejosRepository itemRepository,
                               UsuarioService usuarioService,
                               JogoService jogoService) {
        this.listaDesejosRepository = listaDesejosRepository;
        this.itemRepository = itemRepository;
        this.usuarioService = usuarioService;
        this.jogoService = jogoService;
    }

    /** Cria a lista de desejos do usuário ou devolve a já existente. */
    public ListaDesejos criarLista(Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        return listaDesejosRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            ListaDesejos lista = new ListaDesejos();
            lista.setUsuario(usuario);
            usuario.setListaDesejos(lista);
            return listaDesejosRepository.save(lista);
        });
    }

    /** Busca a lista de desejos do usuário ou falha se ele ainda não tiver uma. */
    public ListaDesejos buscarPorUsuario(Long usuarioId) {
        return listaDesejosRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lista de desejos não encontrada para o usuário: " + usuarioId));
    }

    /** Lista os usuários que têm o jogo em sua lista de desejos. */
    public List<Usuario> buscarUsuariosInteressados(Long jogoId) {
        jogoService.buscarPorId(jogoId);
        return itemRepository.buscarUsuariosPorJogo(jogoId);
    }

    /** Remove a lista de desejos de um usuário. */
    public void deletarLista(Long usuarioId, Long listaId) {
        ListaDesejos lista = buscarPorUsuario(usuarioId);
        if (!lista.getId().equals(listaId)) {
            throw new RecursoNaoEncontradoException(
                    "A lista " + listaId + " não pertence ao usuário " + usuarioId + ".");
        }
        lista.getUsuario().setListaDesejos(null);
        listaDesejosRepository.delete(lista);
    }
}
