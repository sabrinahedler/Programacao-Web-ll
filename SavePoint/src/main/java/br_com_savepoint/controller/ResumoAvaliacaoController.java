package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.ResumoAvaliacao;
import br_com_savepoint.service.ResumoAvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resumo de avaliações", description = "Resumo consolidado das avaliações de um jogo")
@RestController
public class ResumoAvaliacaoController {

    private final ResumoAvaliacaoService resumoService;

    public ResumoAvaliacaoController(ResumoAvaliacaoService resumoService) {
        this.resumoService = resumoService;
    }

    @Operation(
            summary = "Gerar resumo de avaliações do jogo",
            description = "Calcula nota média, total e percentual de recomendação. Se já existir um resumo, ele é recalculado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resumo gerado"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @PostMapping("/jogos/{id}/resumo")
    public ResponseEntity<ResumoAvaliacao> gerarResumo(@PathVariable("id") Long jogoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resumoService.gerarResumo(jogoId));
    }

    @Operation(
            summary = "Buscar resumo de avaliações do jogo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/jogos/{id}/resumo")
    public ResponseEntity<ResumoAvaliacao> buscarResumo(@PathVariable("id") Long jogoId) {
        return ResponseEntity.ok(resumoService.buscarResumo(jogoId));
    }

    @Operation(
            summary = "Atualizar resumo de avaliações do jogo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "resumoGeradoIA": "Jogo muito elogiado pela narrativa e pelo mundo aberto.",
                              "principaisElogios": ["Narrativa envolvente", "Trilha sonora"],
                              "principaisCriticas": ["Bugs no lancamento"]
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resumo atualizado"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PutMapping("/jogos/{id}/resumo")
    public ResponseEntity<ResumoAvaliacao> atualizarResumo(@PathVariable("id") Long jogoId,
                                                           @Valid @RequestBody ResumoAvaliacao resumo) {
        return ResponseEntity.ok(resumoService.atualizarResumo(jogoId, resumo));
    }

    @Operation(
            summary = "Excluir resumo de avaliações",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @DeleteMapping("/jogos/{jogoId}/resumo/{resumoId}")
    public ResponseEntity<Void> deletarResumo(@PathVariable Long jogoId, @PathVariable Long resumoId) {
        resumoService.deletarResumo(jogoId, resumoId);
        return ResponseEntity.noContent().build();
    }
}
