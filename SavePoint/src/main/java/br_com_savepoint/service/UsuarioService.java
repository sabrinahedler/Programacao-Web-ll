package br_com_savepoint.service;

import br_com_savepoint.exception.RecursoNaoEncontradoException;
import br_com_savepoint.exception.RegraNegocioException;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.UsuarioRepository;
import br_com_savepoint.util.Validacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

/** Regras de negócio do cadastro de usuários. */
@Service
@Transactional
public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Lista todos os usuários. */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /** Lista apenas os usuários ativos. */
    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    /** Lista apenas os usuários inativos. */
    public List<Usuario> listarInativos() {
        return usuarioRepository.findByAtivoFalse();
    }

    /** Busca um usuário pelo identificador ou falha se ele não existir. */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o ID: " + id));
    }

    /** Busca um usuário pelo e-mail ou falha se ele não existir. */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o e-mail: " + email));
    }

    /** Cadastra um usuário após validar nome, e-mail, senha e unicidade do e-mail. */
    public Usuario salvar(Usuario usuario) {
        Validacao.exigirTexto(usuario.getNome(), "nome");
        Validacao.exigirTexto(usuario.getEmail(), "email");
        validarSenha(usuario.getSenha());

        String email = normalizarEmail(usuario.getEmail());
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("E-mail já cadastrado: " + email);
        }

        usuario.setId(null);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setUltimoAcesso(null);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    /** Atualiza nome, e-mail e telefone do usuário, ignorando os campos não informados. */
    public Usuario atualizar(Long id, Usuario dados) {
        Usuario usuario = buscarPorId(id);

        if (dados.getNome() != null && !dados.getNome().trim().isEmpty()) {
            usuario.setNome(dados.getNome());
        }
        if (dados.getEmail() != null && !dados.getEmail().trim().isEmpty()) {
            String email = normalizarEmail(dados.getEmail());
            usuarioRepository.findByEmail(email).ifPresent(outro -> {
                if (!outro.getId().equals(id)) {
                    throw new RegraNegocioException("E-mail já cadastrado por outro usuário.");
                }
            });
            usuario.setEmail(email);
        }
        if (dados.getTelefone() != null) {
            usuario.setTelefone(dados.getTelefone());
        }
        return usuarioRepository.save(usuario);
    }

    /** Altera a senha do usuário após validar o tamanho mínimo. */
    public Usuario atualizarSenha(Long id, String novaSenha) {
        Usuario usuario = buscarPorId(id);
        validarSenha(novaSenha);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        return usuarioRepository.save(usuario);
    }

    /** Marca o usuário como ativo. */
    public Usuario ativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.ativar();
        return usuarioRepository.save(usuario);
    }

    /** Marca o usuário como inativo. */
    public Usuario desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.desativar();
        return usuarioRepository.save(usuario);
    }

    /** Remove um usuário e os dados que dependem dele. */
    public void deletar(Long id) {
        usuarioRepository.delete(buscarPorId(id));
    }

    /** Conta o total de usuários. */
    public long contarTotal() {
        return usuarioRepository.count();
    }

    /** Conta os usuários ativos. */
    public long contarAtivos() {
        return usuarioRepository.countByAtivoTrue();
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraNegocioException("A senha deve ter no mínimo " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }

    private String normalizarEmail(String email) {
        String normalizado = email.trim().toLowerCase();
        if (!normalizado.contains("@")) {
            throw new RegraNegocioException("E-mail inválido: " + email);
        }
        return normalizado;
    }
}
