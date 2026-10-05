package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.AvaliacaoUsuario;
import br_com_savepoint.service.AvaliacaoUsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Avaliações", description = "Avaliações dos usuários sobre os jogos")
@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoUsuarioController {

    private final AvaliacaoUsuarioService avaliacaoService;

    public AvaliacaoUsuarioController(AvaliacaoUsuarioService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @Operation(
            summary = "Listar todas as avaliações",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de avaliações")
            })
    @GetMapping
    public ResponseEntity<List<AvaliacaoUsuario>> listarTodas() {
        return ResponseEntity.ok(avaliacaoService.listarTodas());
    }

    @Operation(
            summary = "Buscar avaliação por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoUsuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.buscarPorId(id));
    }

    @Operation(
            summary = "Listar avaliações de um jogo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de avaliações"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @GetMapping("/jogo/{jogoId}")
    public ResponseEntity<List<AvaliacaoUsuario>> buscarPorJogo(@PathVariable Long jogoId) {
        return ResponseEntity.ok(avaliacaoService.buscarPorJogo(jogoId));
    }

    @Operation(
            summary = "Listar avaliações de um usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de avaliações"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AvaliacaoUsuario>> buscarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(avaliacaoService.buscarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Calcular nota média do jogo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nota média"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @GetMapping("/jogo/{jogoId}/media")
    public ResponseEntity<Double> calcularMedia(@PathVariable Long jogoId) {
        return ResponseEntity.ok(avaliacaoService.calcularMediaJogo(jogoId));
    }

    @Operation(
            summary = "Calcular índice de recomendação do jogo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Percentual de avaliações com nota 4 ou 5"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @GetMapping("/jogo/{jogoId}/indice-recomendacao")
    public ResponseEntity<Double> calcularIndiceRecomendacao(@PathVariable Long jogoId) {
        return ResponseEntity.ok(avaliacaoService.calcularIndiceRecomendacao(jogoId));
    }

    @Operation(
            summary = "Contar avaliações de um jogo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total de avaliações"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @GetMapping("/jogo/{jogoId}/count")
    public ResponseEntity<Long> contarPorJogo(@PathVariable Long jogoId) {
        return ResponseEntity.ok(avaliacaoService.contarPorJogo(jogoId));
    }

    @Operation(
            summary = "Verificar se a avaliação recomenda o jogo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "true se a nota for 4 ou 5"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/{id}/recomendado")
    public ResponseEntity<Boolean> isRecomendado(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.isRecomendado(id));
    }

    @Operation(
            summary = "Cadastrar avaliação",
            description = "Usuário e jogo são informados apenas pelo id.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "nota": 5,
                              "textoAvaliacao": "História incrível e mundo muito bem construído.",
                              "horasJogadas": 120,
                              "indiceRecomendacao": true,
                              "usuario": { "id": 1 },
                              "jogo": { "id": 1 }
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Nota fora do intervalo de 1 a 5, texto vazio ou dados ausentes"),
                    @ApiResponse(responseCode = "404", description = "Usuário ou jogo não encontrado")
            })
    @PostMapping
    public ResponseEntity<AvaliacaoUsuario> criar(@Valid @RequestBody AvaliacaoUsuario avaliacao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoService.salvar(avaliacao));
    }

    @Operation(
            summary = "Curtir avaliação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curtida registrada"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PostMapping("/{id}/curtir")
    public ResponseEntity<AvaliacaoUsuario> curtir(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.curtir(id));
    }

    @Operation(
            summary = "Atualizar avaliação",
            description = "Atualiza nota, texto e horas jogadas.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "nota": 5,
                              "textoAvaliacao": "História incrível e mundo muito bem construído.",
                              "horasJogadas": 120,
                              "indiceRecomendacao": true,
                              "usuario": { "id": 1 },
                              "jogo": { "id": 1 }
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Avaliação atualizada"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoUsuario> atualizar(@PathVariable Long id, @Valid @RequestBody AvaliacaoUsuario avaliacao) {
        return ResponseEntity.ok(avaliacaoService.atualizar(id, avaliacao));
    }

    @Operation(
            summary = "Excluir avaliação",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        avaliacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Excluir todas as avaliações de um jogo",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @DeleteMapping("/jogo/{jogoId}")
    public ResponseEntity<Void> deletarPorJogo(@PathVariable Long jogoId) {
        avaliacaoService.deletarPorJogo(jogoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Excluir todas as avaliações de um usuário",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            })
    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<Void> deletarPorUsuario(@PathVariable Long usuarioId) {
        avaliacaoService.deletarPorUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
