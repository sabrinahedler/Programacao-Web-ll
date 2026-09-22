package br_com_savepoint.service;

import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // LISTAGENS 

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    public List<Usuario> listarInativos() {
        return usuarioRepository.findByAtivoFalse();
    }

    //BUSCAS 

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // CRUD

    public Usuario salvar(Usuario usuario) throws Exception {
        // Validações
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new Exception("Nome é obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new Exception("Email é obrigatório");
        }
        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new Exception("Senha deve ter no mínimo 6 caracteres");
        }

        // Verifica se email já existe
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
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + id);
        }

        // Atualiza apenas campos permitidos
        if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().trim().isEmpty()) {
            usuario.setNome(usuarioAtualizado.getNome());
        }
        if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().trim().isEmpty()) {
            // Verifica se o novo email já existe em outro usuário

 Usuario outroUsuario = usuarioRepository.findByEmail(usuarioAtualizado.getEmail()).orElse(null);
            if (outroUsuario != null && !outroUsuario.getId().equals(id)) {
                throw new Exception("Email já cadastrado por outro usuário");
            }
            usuario.setEmail(usuarioAtualizado.getEmail());
        }
        if (usuarioAtualizado.getTelefone() != null) {
            usuario.setTelefone(usuarioAtualizado.getTelefone());
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

        usuario.setSenha(novaSenha);
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

    public void deletar(Long id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    

    public long contarTotal() {
        return usuarioRepository.count();
  }
    public long contarAtivos() {
        return usuarioRepository.findByAtivoTrue().size();
    }
}