package br_com_savepoint.model;

import br.com.savepoint.savepoint.model.AvaliacaoUsuario;
import br.com.savepoint.savepoint.model.Jogo;
import br.com.savepoint.savepoint.model.Usuario;
import br.com.savepoint.savepoint.repository.AvaliacaoUsuarioRepositorio;
import br.com.savepoint.savepoint.repository.JogoRepositorio;
import br.com.savepoint.savepoint.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoUsuarioServico {

    @Autowired
    private AvaliacaoUsuarioRepositorio avaliacaoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private JogoRepositorio jogoRepositorio;

    public List<AvaliacaoUsuario> listarTodas() {
        return avaliacaoRepositorio.listarTodos();
    }

    public AvaliacaoUsuario buscarPorId(Long id) {
        return avaliacaoRepositorio.buscarPorId(id);
    }

    public List<AvaliacaoUsuario> buscarPorJogo(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.buscarPorId(jogoId);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        return avaliacaoRepositorio.buscarPorJogoId(jogoId);
    }

    public List<AvaliacaoUsuario> buscarPorUsuario(Long usuarioId) throws Exception {
        Usuario usuario = usuarioRepositorio.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + usuarioId);
        }
        return avaliacaoRepositorio.buscarPorUsuarioId(usuarioId);
    }

    public double calcularMediaJogo(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.buscarPorId(jogoId);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        return avaliacaoRepositorio.calcularMediaJogo(jogoId);
    }

    public double calcularIndiceRecomendacao(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.buscarPorId(jogoId);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        return avaliacaoRepositorio.calcularPercentualRecomendacao(jogoId);
    }

    public AvaliacaoUsuario salvar(AvaliacaoUsuario avaliacao) throws Exception {
        if (avaliacao.getUsuario() == null) {
            throw new Exception("Usuário é obrigatório");
        }
        if (avaliacao.getJogo() == null) {
            throw new Exception("Jogo é obrigatório");
        }
        
        Usuario usuario = usuarioRepositorio.buscarPorId(avaliacao.getUsuario().getId());
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + avaliacao.getUsuario().getId());
        }
        
        Jogo jogo = jogoRepositorio.buscarPorId(avaliacao.getJogo().getId());
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + avaliacao.getJogo().getId());
        }
        
        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new Exception("Nota deve ser entre 1 e 5");
        }
        
        if (avaliacao.getTextoAvaliacao() == null || avaliacao.getTextoAvaliacao().trim().isEmpty()) {
            throw new Exception("Texto da avaliação é obrigatório");
        }
          return avaliacaoRepositorio.salvar(avaliacao);
    }

    public AvaliacaoUsuario atualizar(Long id, AvaliacaoUsuario avaliacaoAtualizada) throws Exception {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.buscarPorId(id);
        if (avaliacao == null) {
            throw new Exception("Avaliação não encontrada com ID: " + id);
        }
        
        if (avaliacaoAtualizada.getNota() >= 1 && avaliacaoAtualizada.getNota() <= 5) { avaliacao.setNota(avaliacaoAtualizada.getNota());
        }
        
        if (avaliacaoAtualizada.getTextoAvaliacao() != null && 
            !avaliacaoAtualizada.getTextoAvaliacao().trim().isEmpty()) {
            avaliacao.setTextoAvaliacao(avaliacaoAtualizada.getTextoAvaliacao());
        }
        
        return avaliacaoRepositorio.salvar(avaliacao);
    }

    public void deletar(Long id) throws Exception {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.buscarPorId(id);
        if (avaliacao == null) {
            throw new Exception("Avaliação não encontrada com ID: " + id);
        }
        avaliacaoRepositorio.deletarPorId(id);
    }

    public void deletarPorJogo(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.buscarPorId(jogoId);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        avaliacaoRepositorio.deletarPorJogoId(jogoId);
    }

    public void deletarPorUsuario(Long usuarioId) throws Exception {
        Usuario usuario = usuarioRepositorio.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + usuarioId);
        }
        avaliacaoRepositorio.deletarPorUsuarioId(usuarioId);
    }

    public AvaliacaoUsuario curtir(Long id) throws Exception {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.buscarPorId(id);
        if (avaliacao == null) {
            throw new Exception("Avaliação não encontrada com ID: " + id);
        }
        avaliacao.curtir();
        return avaliacaoRepositorio.salvar(avaliacao);
    }

    public AvaliacaoUsuario marcarComoUtil(Long id) throws Exception {
        AvaliacaoUsuario avaliacao = avaliacaoRepositorio.buscarPorId(id);
        if (avaliacao == null) {
            throw new Exception("Avaliação não encontrada com ID: " + id);
        }
        avaliacao.marcarComoUtil();
        return avaliacaoRepositorio.salvar(avaliacao);
    }

    public long contarPorJogo(Long jogoId) throws Exception {
        Jogo jogo = jogoRepositorio.buscarPorId(jogoId);
        if (jogo == null) {
            throw new Exception("Jogo não encontrado com ID: " + jogoId);
        }
        return avaliacaoRepositorio.contarPorJogoId(jogoId);
    }

    public boolean isRecomendado(AvaliacaoUsuario avaliacao) {
        return avaliacao.isRecomendado();
    }
}