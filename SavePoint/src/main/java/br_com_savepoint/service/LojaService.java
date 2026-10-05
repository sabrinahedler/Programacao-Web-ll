package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.exception.RegraNegocioException;
import br_com_savepoint.model.Loja;
import br_com_savepoint.repository.LojaRepository;
import br_com_savepoint.util.Validacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio do cadastro de lojas. */
@Service
@Transactional
public class LojaService {

    private final LojaRepository lojaRepository;

    public LojaService(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    /** Lista todas as lojas cadastradas. */
    public List<Loja> listarTodas() {
        return lojaRepository.findAll();
    }

    /** Busca uma loja pelo identificador ou falha se ela não existir. */
    public Loja buscarPorId(Long id) {
        return lojaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Loja não encontrada com o ID: " + id));
    }

    /** Cadastra uma nova loja com nome único. */
    public Loja salvar(Loja loja) {
        validarCampos(loja);
        validarNomeDisponivel(loja.getNome(), null);
        loja.setId(null);
        return lojaRepository.save(loja);
    }

    /** Atualiza os dados de uma loja existente. */
    public Loja atualizar(Long id, Loja dados) {
        Loja loja = buscarPorId(id);
        validarCampos(dados);
        validarNomeDisponivel(dados.getNome(), id);
        loja.setNome(dados.getNome());
        loja.setUrlLoja(dados.getUrlLoja());
        loja.setUrlLogo(dados.getUrlLogo());
        return lojaRepository.save(loja);
    }

    /** Remove uma loja e as ofertas associadas a ela. */
    public void deletar(Long id) {
        lojaRepository.delete(buscarPorId(id));
    }

    private void validarCampos(Loja loja) {
        Validacao.exigirTexto(loja.getNome(), "nome");
        Validacao.exigirTexto(loja.getUrlLoja(), "urlLoja");
        Validacao.exigirTexto(loja.getUrlLogo(), "urlLogo");
    }

    private void validarNomeDisponivel(String nome, Long idAtual) {
        lojaRepository.findByNomeIgnoreCase(nome.trim()).ifPresent(existente -> {
            if (!existente.getId().equals(idAtual)) {
                throw new RegraNegocioException("Já existe uma loja cadastrada com este nome.");
            }
        });
    }
}
