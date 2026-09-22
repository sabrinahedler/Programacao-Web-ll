package br_com_savepoint.controller;

import br_com_savepoint.model.Usuario;
import br_com_savepoint.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

@Autowired
private UsuarioService usuarioService;

    //LISTAR TODOS

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
    return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
    }

    // LISTAR ATIVOS 

    @GetMapping("/ativos")
    public ResponseEntity<List<Usuario>> listarAtivos() {
  List<Usuario> usuarios = usuarioService.listarAtivos();
        return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
    }

    // LISTAR INATIVOS 

    @GetMapping("/inativos")
    public ResponseEntity<List<Usuario>> listarInativos() {
        List<Usuario> usuarios = usuarioService.listarInativos();
        return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
    }

    //  BUSCAR POR ID 

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        }
        return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
    }

// BUSCAR POR EMAIL

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario != null) {
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        }
        return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
    }

    //CRIAR 

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.salvar(usuario);
            return new ResponseEntity<Usuario>(novoUsuario, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ATUALIZAR

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
            return new ResponseEntity<Usuario>(usuarioAtualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //ATUALIZAR SENHA

    @PatchMapping("/{id}/senha")
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id, @RequestBody String novaSenha) {
        try {
            Usuario usuario = usuarioService.atualizarSenha(id, novaSenha);
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //  ATIVAR

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.ativar(id);
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //  DESATIVAR

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.desativar(id);
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // DELETAR

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }

    // CONTAGENS 

    @GetMapping("/count")
    public ResponseEntity<Long> contarTotal() {
        return new ResponseEntity<Long>(usuarioService.contarTotal(), HttpStatus.OK);
    }

    @GetMapping("/ativos/count")
    public ResponseEntity<Long> contarAtivos() {
        return new ResponseEntity<Long>(usuarioService.contarAtivos(), HttpStatus.OK);
    }
}