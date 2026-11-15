package br_com_savepoint.service;

import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    public List<Usuario> listarInativos() {
        return usuarioRepository.findByAtivoFalse();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario login(String email, String senha) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isPresent() && usuarioOptional.get().getSenha().equals(senha)) {
            return usuarioOptional.get();
        }
        throw new IllegalArgumentException("E-mail ou senha inválidos.");
    }

    public Usuario salvar(Usuario usuario) throws Exception {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new Exception("Nome é obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new Exception("Email é obrigatório");
        }
        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new Exception("Senha deve ter no mínimo 6 caracteres");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Exception("Email já cadastrado: " + usuario.getEmail());
        }
        if (usuario.getDataCadastro() == null) {
            usuario.setDataCadastro(LocalDateTime.now());
        }
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Long id, Usuario usuarioAtualizado) throws Exception {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().trim().isEmpty()) {
            String novoNome = usuarioAtualizado.getNome().trim();
            Usuario outroUsuario = usuarioRepository.findByNomeIgnoreCase(novoNome).orElse(null);
            if (outroUsuario != null && !outroUsuario.getId().equals(id)) {
                throw new Exception("Este nickname já está em uso.");
            }
            usuario.setNome(novoNome);
        }
        if (usuarioAtualizado.getTelefone() != null) {
            usuario.setTelefone(usuarioAtualizado.getTelefone());
        }

        // foto de perfil só aceita imagens da galeria do próprio site
        if (usuarioAtualizado.getFotoPerfil() != null) {
            String foto = usuarioAtualizado.getFotoPerfil().trim();
            if (!foto.isEmpty() && !foto.matches("/assets/avatares/[A-Za-z0-9_-]+\\.(png|jpg|jpeg|webp|svg)")) {
                throw new Exception("Foto de perfil inválida.");
            }
            usuario.setFotoPerfil(foto.isEmpty() ? null : foto);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarSenha(Long id, String novaSenha) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + id);
        }

        if (novaSenha == null || novaSenha.length() < 6) {
            throw new Exception("Senha deve ter no mínimo 6 caracteres");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUltimoAcesso(Long id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + id);
        }

        usuario.setUltimoAcesso(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario ativar(Long id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + id);
        }
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario desativar(Long id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + id);
        }
        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public long contarTotal() {
        return usuarioRepository.count();
    }

    public long contarAtivos() {
        return usuarioRepository.countByAtivoTrue();
    }
}