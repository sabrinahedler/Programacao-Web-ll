package br_com_savepoint.controller;

import br_com_savepoint.dto.AtualizarSenhaRequest;
import br_com_savepoint.model.RegisterDTO;
import br_com_savepoint.model.UserRoles;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.service.UsuarioService;
import br_com_savepoint.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import br_com_savepoint.model.UserRoles;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Usuario>> listarAtivos() {
        List<Usuario> usuarios = usuarioService.listarAtivos();
        return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/inativos")
    public ResponseEntity<List<Usuario>> listarInativos() {
        List<Usuario> usuarios = usuarioService.listarInativos();
        return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<Usuario> meusDados(@AuthenticationPrincipal Usuario logado) {
        if (logado == null) {
            return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
        }
        Usuario usuario = usuarioService.buscarPorId(logado.getId());
        if (usuario == null) {
            return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Usuario logado) {
        if (!podeAcessar(logado, id)) {
            return new ResponseEntity<Usuario>(HttpStatus.FORBIDDEN);
        }
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        }
        return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario != null) {
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        }
        return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Usuario usuario, @AuthenticationPrincipal Usuario logado) {
        // cada usuário só altera o próprio cadastro, ADMIN altera qualquer um
        if (logado == null || (logado.getRole() != UserRoles.ADMIN && !logado.getId().equals(id))) {
            return new ResponseEntity<String>("Acesso negado.", HttpStatus.FORBIDDEN);
        }
        try {
            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
            return new ResponseEntity<Usuario>(usuarioAtualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id, @RequestBody @Valid AtualizarSenhaRequest request,
                                            @AuthenticationPrincipal Usuario logado) {
        if (!podeAcessar(logado, id)) {
            return new ResponseEntity<String>("Acesso negado.", HttpStatus.FORBIDDEN);
        }
        try {
            Usuario usuario = usuarioService.atualizarSenha(id, request.novaSenha());
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.ativar(id);
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.desativar(id);
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contarTotal() {
        return new ResponseEntity<Long>(usuarioService.contarTotal(), HttpStatus.OK);
    }

    @GetMapping("/ativos/count")
    public ResponseEntity<Long> contarAtivos() {
        return new ResponseEntity<Long>(usuarioService.contarAtivos(), HttpStatus.OK);
    }

    @PostMapping("/admin")
    public ResponseEntity<?> criarAdministrador(@RequestBody @Valid RegisterDTO data) {
        if (this.usuarioRepository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        String senhaCriptografada = new BCryptPasswordEncoder().encode(data.senha());

        Usuario novoAdmin = new Usuario(data.nome(), data.email(), senhaCriptografada, UserRoles.ADMIN);
        novoAdmin.setAtivo(true);
        novoAdmin.setDataCadastro(LocalDateTime.now());

        this.usuarioRepository.save(novoAdmin);

        return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of("id", novoAdmin.getId(), "nome", novoAdmin.getNome(), "email", novoAdmin.getEmail(), "role", novoAdmin.getRole().name()));
    }

    /** ADMIN acessa qualquer usuário; USER só o próprio cadastro. */
    private boolean podeAcessar(Usuario logado, Long id) {
        return logado != null && (logado.getRole() == UserRoles.ADMIN || logado.getId().equals(id));
    }
}
