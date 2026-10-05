package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.dto.AtualizarSenhaRequest;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Usuários", description = "Cadastro e gestão de usuários")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Listar todos os usuários",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários")
            })
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(
            summary = "Listar usuários ativos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários ativos")
            })
    @GetMapping("/ativos")
    public ResponseEntity<List<Usuario>> listarAtivos() {
        return ResponseEntity.ok(usuarioService.listarAtivos());
    }

    @Operation(
            summary = "Listar usuários inativos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários inativos")
            })
    @GetMapping("/inativos")
    public ResponseEntity<List<Usuario>> listarInativos() {
        return ResponseEntity.ok(usuarioService.listarInativos());
    }

    @Operation(
            summary = "Contar usuários",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total de usuários")
            })
    @GetMapping("/count")
    public ResponseEntity<Long> contarTotal() {
        return ResponseEntity.ok(usuarioService.contarTotal());
    }

    @Operation(
            summary = "Contar usuários ativos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total de usuários ativos")
            })
    @GetMapping("/ativos/count")
    public ResponseEntity<Long> contarAtivos() {
        return ResponseEntity.ok(usuarioService.contarAtivos());
    }

    @Operation(
            summary = "Buscar usuário por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @Operation(
            summary = "Buscar usuário por e-mail",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @Operation(
            summary = "Cadastrar usuário",
            description = "A senha deve ter no mínimo 6 caracteres e nunca é devolvida nas respostas.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "nome": "Jean Silva",
                              "email": "jean@exemplo.com",
                              "senha": "senha123",
                              "telefone": "47999990000"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
            })
    @PostMapping
    public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.salvar(usuario));
    }

    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza nome, e-mail e telefone. Campos não informados são mantidos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "nome": "Jean Silva",
                              "email": "jean@exemplo.com",
                              "senha": "senha123",
                              "telefone": "47999990000"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
                    @ApiResponse(responseCode = "400", description = "E-mail inválido ou já cadastrado"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.atualizar(id, usuario));
    }

    @Operation(
            summary = "Atualizar senha do usuário",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "novaSenha": "novaSenha456"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Senha atualizada"),
                    @ApiResponse(responseCode = "400", description = "Senha inválida"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PatchMapping("/{id}/senha")
    public ResponseEntity<Usuario> atualizarSenha(@PathVariable Long id, @Valid @RequestBody AtualizarSenhaRequest requisicao) {
        return ResponseEntity.ok(usuarioService.atualizarSenha(id, requisicao.novaSenha()));
    }

    @Operation(
            summary = "Ativar usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário ativado"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Usuario> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.ativar(id));
    }

    @Operation(
            summary = "Desativar usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário desativado"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Usuario> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desativar(id));
    }

    @Operation(
            summary = "Excluir usuário",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
