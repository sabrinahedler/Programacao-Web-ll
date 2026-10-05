package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.Jogo;
import br_com_savepoint.service.JogoService;
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

@Tag(name = "Jogos", description = "Cadastro e consulta de jogos")
@RestController
@RequestMapping("/jogos")
public class JogoController {

    private final JogoService jogoService;

    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @Operation(
            summary = "Listar todos os jogos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de jogos")
            })
    @GetMapping
    public ResponseEntity<List<Jogo>> listarTodos() {
        return ResponseEntity.ok(jogoService.listarTodos());
    }

    @Operation(
            summary = "Buscar jogo por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Jogo> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jogoService.buscarPorId(id));
    }

    @Operation(
            summary = "Cadastrar jogo",
            description = "Cria um jogo. Requisitos mínimos, ofertas e resumo são cadastrados pelos endpoints próprios.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "titulo": "The Witcher 3: Wild Hunt",
                              "descricao": "RPG de mundo aberto onde o jogador vive Geralt de Rivia.",
                              "dataLancamento": "2015-05-19",
                              "imagemCapa": "https://exemplo.com/capas/witcher3.jpg",
                              "classificacaoIndicativa": "18",
                              "desenvolvedora": "CD Projekt Red",
                              "genero": "RPG"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            })
    @PostMapping
    public ResponseEntity<Jogo> salvar(@Valid @RequestBody Jogo jogo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jogoService.salvar(jogo));
    }

    @Operation(
            summary = "Atualizar jogo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "titulo": "The Witcher 3: Wild Hunt",
                              "descricao": "RPG de mundo aberto onde o jogador vive Geralt de Rivia.",
                              "dataLancamento": "2015-05-19",
                              "imagemCapa": "https://exemplo.com/capas/witcher3.jpg",
                              "classificacaoIndicativa": "18",
                              "desenvolvedora": "CD Projekt Red",
                              "genero": "RPG"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Jogo atualizado"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PutMapping("/{id}")
    public ResponseEntity<Jogo> atualizar(@PathVariable Long id, @Valid @RequestBody Jogo jogo) {
        return ResponseEntity.ok(jogoService.atualizar(id, jogo));
    }

    @Operation(
            summary = "Excluir jogo",
            description = "Remove também as ofertas, o histórico, as avaliações e os itens de lista de desejos do jogo.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        jogoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
