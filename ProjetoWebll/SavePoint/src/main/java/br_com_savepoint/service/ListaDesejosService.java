package br_com_savepoint.service;

import br_com_savepoint.model.ItemListaDesejos;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.ListaDesejos;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.ItemListaDesejosRepository;
import br_com_savepoint.repository.JogoRepository;
import br_com_savepoint.repository.ListaDesejosRepository;
import br_com_savepoint.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ListaDesejosService {

    private final ListaDesejosRepository listaDesejosRepository;
    private final ItemListaDesejosRepository itemListaDesejosRepository;
    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;

    public ListaDesejosService(
            ListaDesejosRepository listaDesejosRepository,
            ItemListaDesejosRepository itemListaDesejosRepository,
            UsuarioRepository usuarioRepository,
            JogoRepository jogoRepository) {
        this.listaDesejosRepository = listaDesejosRepository;
        this.itemListaDesejosRepository = itemListaDesejosRepository;
        this.usuarioRepository = usuarioRepository;
        this.jogoRepository = jogoRepository;
    }

    // POST /usuarios/{id}/lista-desejos/
    public ListaDesejos criarLista(Long usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado com o ID: " + usuarioId);
        }

        Optional<ListaDesejos> listaExistente =
                listaDesejosRepository.findByUsuarioId(usuarioId);

        if (listaExistente.isPresent()) {
            return listaExistente.get();
        }

        ListaDesejos novaLista = new ListaDesejos();
        novaLista.setUsuario(usuarioOptional.get());

        return listaDesejosRepository.save(novaLista);
    }

    // GET /jogos/{idJogo}/usuarios-interessados
    public List<Usuario> buscarUsuariosInteressados(Long jogoId) {
        Optional<Jogo> jogoOptional = jogoRepository.findById(jogoId);

        if (jogoOptional.isEmpty()) {
            throw new IllegalArgumentException("Jogo não encontrado com o ID: " + jogoId);
        }

        List<Usuario> usuariosInteressados = new ArrayList<>();
        List<ItemListaDesejos> itens = itemListaDesejosRepository.findAll();

        for (ItemListaDesejos item : itens) {
            if (item.getJogo() != null
                    && item.getJogo().getId() != null
                    && item.getJogo().getId().equals(jogoId)
                    && item.getListaDesejos() != null
                    && item.getListaDesejos().getUsuario() != null) {

                Usuario usuario = item.getListaDesejos().getUsuario();

                if (!usuariosInteressados.contains(usuario)) {
                    usuariosInteressados.add(usuario);
                }
            }
        }

        return usuariosInteressados;
    }

    // DELETE /usuarios/{usuarioId}/lista-desejos/{listaId}
    public boolean deletarLista(Long usuarioId, Long listaId) {
        Optional<ListaDesejos> listaOptional = listaDesejosRepository.findById(listaId);

        if (listaOptional.isEmpty()) {
            return false;
        }

        ListaDesejos lista = listaOptional.get();

        if (lista.getUsuario() == null
                || !lista.getUsuario().getId().equals(usuarioId)) {
            return false;
        }

        listaDesejosRepository.deleteById(listaId);
        return true;
    }
}
