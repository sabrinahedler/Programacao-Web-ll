package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.repository.JogoRepository;
import br_com_savepoint.util.Validacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/** Regras de negócio do cadastro de jogos. */
@Service
@Transactional
public class JogoService {

    private final JogoRepository jogoRepository;

    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }

    /** Lista todos os jogos cadastrados. */
    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }

    /** Busca um jogo pelo identificador ou falha se ele não existir. */
    public Jogo buscarPorId(Long id) {
        return jogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo não encontrado com o ID: " + id));
    }

    /** Cadastra um novo jogo após validar os campos obrigatórios. */
    public Jogo salvar(Jogo jogo) {
        validarCampos(jogo);
        jogo.setId(null);
        jogo.setRequisitosMinimos(null);
        jogo.setResumoAvaliacao(null);
        jogo.setOfertas(new ArrayList<>());
        return jogoRepository.save(jogo);
    }

    /** Atualiza os dados básicos de um jogo existente. */
    public Jogo atualizar(Long id, Jogo dados) {
        Jogo jogo = buscarPorId(id);
        validarCampos(dados);
        jogo.setTitulo(dados.getTitulo());
        jogo.setDescricao(dados.getDescricao());
        jogo.setDataLancamento(dados.getDataLancamento());
        jogo.setImagemCapa(dados.getImagemCapa());
        jogo.setClassificacaoIndicativa(dados.getClassificacaoIndicativa());
        jogo.setDesenvolvedora(dados.getDesenvolvedora());
        jogo.setGenero(dados.getGenero());
        return jogoRepository.save(jogo);
    }

    /** Remove um jogo e os dados que dependem dele. */
    public void deletar(Long id) {
        jogoRepository.delete(buscarPorId(id));
    }

    private void validarCampos(Jogo jogo) {
        Validacao.exigirTexto(jogo.getTitulo(), "titulo");
        Validacao.exigirTexto(jogo.getDescricao(), "descricao");
        Validacao.exigirPreenchido(jogo.getDataLancamento(), "dataLancamento");
        Validacao.exigirTexto(jogo.getClassificacaoIndicativa(), "classificacaoIndicativa");
        Validacao.exigirTexto(jogo.getDesenvolvedora(), "desenvolvedora");
        Validacao.exigirTexto(jogo.getGenero(), "genero");
    }
}
