package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.exception.RegraNegocioException;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.model.RequisitosMinimos;
import br_com_savepoint.repository.RequisitosMinimosRepository;
import br_com_savepoint.util.Validacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras de negócio dos requisitos mínimos de um jogo. */
@Service
@Transactional
public class RequisitosMinimosService {

    private final RequisitosMinimosRepository requisitosRepository;
    private final JogoService jogoService;

    public RequisitosMinimosService(RequisitosMinimosRepository requisitosRepository, JogoService jogoService) {
        this.requisitosRepository = requisitosRepository;
        this.jogoService = jogoService;
    }

    /** Busca os requisitos mínimos de um jogo ou falha se ainda não existirem. */
    public RequisitosMinimos buscarPorJogoId(Long jogoId) {
        Jogo jogo = jogoService.buscarPorId(jogoId);
        if (jogo.getRequisitosMinimos() == null) {
            throw new RecursoNaoEncontradoException("O jogo " + jogoId + " não possui requisitos mínimos cadastrados.");
        }
        return jogo.getRequisitosMinimos();
    }

    /** Cadastra os requisitos mínimos de um jogo que ainda não os possui. */
    public RequisitosMinimos salvar(Long jogoId, RequisitosMinimos requisitos) {
        Jogo jogo = jogoService.buscarPorId(jogoId);
        if (jogo.getRequisitosMinimos() != null) {
            throw new RegraNegocioException("O jogo " + jogoId + " já possui requisitos mínimos cadastrados.");
        }
        validarCampos(requisitos);

        requisitos.setId(null);
        requisitos.setJogo(jogo);
        RequisitosMinimos salvo = requisitosRepository.save(requisitos);
        jogo.setRequisitosMinimos(salvo);
        return salvo;
    }

    /** Atualiza os requisitos mínimos existentes de um jogo. */
    public RequisitosMinimos atualizar(Long jogoId, RequisitosMinimos dados) {
        RequisitosMinimos requisitos = buscarPorJogoId(jogoId);
        validarCampos(dados);

        requisitos.setProcessador(dados.getProcessador());
        requisitos.setMemoria(dados.getMemoria());
        requisitos.setPlacaDeVideo(dados.getPlacaDeVideo());
        requisitos.setSistemaOperacional(dados.getSistemaOperacional());
        return requisitosRepository.save(requisitos);
    }

    /** Remove os requisitos mínimos de um jogo. */
    public void deletarPorJogoId(Long jogoId) {
        Jogo jogo = jogoService.buscarPorId(jogoId);
        RequisitosMinimos requisitos = buscarPorJogoId(jogoId);
        jogo.setRequisitosMinimos(null);
        requisitosRepository.delete(requisitos);
    }

    private void validarCampos(RequisitosMinimos requisitos) {
        Validacao.exigirTexto(requisitos.getProcessador(), "processador");
        Validacao.exigirTexto(requisitos.getMemoria(), "memoria");
        Validacao.exigirTexto(requisitos.getPlacaDeVideo(), "placaDeVideo");
        Validacao.exigirTexto(requisitos.getSistemaOperacional(), "sistemaOperacional");
    }
}
