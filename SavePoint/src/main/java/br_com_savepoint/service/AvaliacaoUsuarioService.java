package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.exception.RegraNegocioException;
import br_com_savepoint.model.AvaliacaoUsuario;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.AvaliacaoUsuarioRepository;
import br_com_savepoint.util.Validacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Regras de negócio das avaliações dos usuários sobre os jogos. */
@Service
@Transactional
public class AvaliacaoUsuarioService {

    private static final int NOTA_MINIMA = 1;
    private static final int NOTA_MAXIMA = 5;
    private static final int NOTA_MINIMA_RECOMENDACAO = 4;

    private final AvaliacaoUsuarioRepository avaliacaoRepository;
    private final UsuarioService usuarioService;
    private final JogoService jogoService;

    public AvaliacaoUsuarioService(AvaliacaoUsuarioRepository avaliacaoRepository,
                                   UsuarioService usuarioService,
                                   JogoService jogoService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioService = usuarioService;
        this.jogoService = jogoService;
    }

    /** Lista todas as avaliações. */
    public List<AvaliacaoUsuario> listarTodas() {
        return avaliacaoRepository.findAll();
    }

    /** Busca uma avaliação pelo identificador ou falha se ela não existir. */
    public AvaliacaoUsuario buscarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliação não encontrada com o ID: " + id));
    }

    /** Lista as avaliações de um jogo. */
    public List<AvaliacaoUsuario> buscarPorJogo(Long jogoId) {
        jogoService.buscarPorId(jogoId);
        return avaliacaoRepository.findByJogoId(jogoId);
    }

    /** Lista as avaliações feitas por um usuário. */
    public List<AvaliacaoUsuario> buscarPorUsuario(Long usuarioId) {
        usuarioService.buscarPorId(usuarioId);
        return avaliacaoRepository.findByUsuarioId(usuarioId);
    }

    /** Calcula a nota média de um jogo, ou zero se não houver avaliações. */
    public double calcularMediaJogo(Long jogoId) {
        jogoService.buscarPorId(jogoId);
        Double media = avaliacaoRepository.calcularMediaPorJogo(jogoId);
        return media != null ? media : 0.0;
    }

    /** Calcula o percentual de avaliações que recomendam o jogo. */
    public double calcularIndiceRecomendacao(Long jogoId) {
        jogoService.buscarPorId(jogoId);
        long total = avaliacaoRepository.countByJogoId(jogoId);
        if (total == 0) {
            return 0.0;
        }
        long recomendadas = avaliacaoRepository.countByJogoIdAndNotaGreaterThanEqual(jogoId, NOTA_MINIMA_RECOMENDACAO);
        return recomendadas * 100.0 / total;
    }

    /** Conta as avaliações de um jogo. */
    public long contarPorJogo(Long jogoId) {
        jogoService.buscarPorId(jogoId);
        return avaliacaoRepository.countByJogoId(jogoId);
    }

    /** Cadastra uma avaliação após validar nota, texto, usuário e jogo. */
    public AvaliacaoUsuario salvar(AvaliacaoUsuario avaliacao) {
        validarNota(avaliacao.getNota());
        Validacao.exigirTexto(avaliacao.getTextoAvaliacao(), "textoAvaliacao");
        Validacao.exigirNaoNegativo(avaliacao.getHorasJogadas(), "horasJogadas");
        Validacao.exigirPreenchido(avaliacao.getUsuario(), "usuario");
        Validacao.exigirPreenchido(avaliacao.getUsuario().getId(), "usuario.id");
        Validacao.exigirPreenchido(avaliacao.getJogo(), "jogo");
        Validacao.exigirPreenchido(avaliacao.getJogo().getId(), "jogo.id");

        Usuario usuario = usuarioService.buscarPorId(avaliacao.getUsuario().getId());
        Jogo jogo = jogoService.buscarPorId(avaliacao.getJogo().getId());

        avaliacao.setId(null);
        avaliacao.setUsuario(usuario);
        avaliacao.setJogo(jogo);
        avaliacao.setDataPublicacao(LocalDateTime.now());
        avaliacao.setCurtidas(0);
        avaliacao.setIndiceRecomendacao(avaliacao.isRecomendado());
        return avaliacaoRepository.save(avaliacao);
    }

    /** Atualiza nota, texto e horas jogadas de uma avaliação. */
    public AvaliacaoUsuario atualizar(Long id, AvaliacaoUsuario dados) {
        AvaliacaoUsuario avaliacao = buscarPorId(id);
        validarNota(dados.getNota());
        Validacao.exigirTexto(dados.getTextoAvaliacao(), "textoAvaliacao");
        Validacao.exigirNaoNegativo(dados.getHorasJogadas(), "horasJogadas");

        avaliacao.setNota(dados.getNota());
        avaliacao.setTextoAvaliacao(dados.getTextoAvaliacao());
        avaliacao.setHorasJogadas(dados.getHorasJogadas());
        avaliacao.setIndiceRecomendacao(avaliacao.isRecomendado());
        return avaliacaoRepository.save(avaliacao);
    }

    /** Remove uma avaliação. */
    public void deletar(Long id) {
        avaliacaoRepository.delete(buscarPorId(id));
    }

    /** Remove todas as avaliações de um jogo. */
    public void deletarPorJogo(Long jogoId) {
        jogoService.buscarPorId(jogoId);
        avaliacaoRepository.deleteByJogoId(jogoId);
    }

    /** Remove todas as avaliações de um usuário. */
    public void deletarPorUsuario(Long usuarioId) {
        usuarioService.buscarPorId(usuarioId);
        avaliacaoRepository.deleteByUsuarioId(usuarioId);
    }

    /** Registra uma curtida na avaliação. */
    public AvaliacaoUsuario curtir(Long id) {
        AvaliacaoUsuario avaliacao = buscarPorId(id);
        avaliacao.curtir();
        return avaliacaoRepository.save(avaliacao);
    }

    /** Indica se a avaliação recomenda o jogo. */
    public boolean isRecomendado(Long id) {
        return buscarPorId(id).isRecomendado();
    }

    private void validarNota(int nota) {
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new RegraNegocioException("A nota deve estar entre " + NOTA_MINIMA + " e " + NOTA_MAXIMA + ".");
        }
    }
}
