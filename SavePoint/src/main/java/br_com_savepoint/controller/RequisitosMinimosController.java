package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.RequisitosMinimos;
import br_com_savepoint.service.RequisitosMinimosService;
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

@Tag(name = "Requisitos mínimos", description = "Requisitos mínimos de hardware de cada jogo")
@RestController
@RequestMapping("/jogos/{id}/requisitos-minimos")
public class RequisitosMinimosController {

    private final RequisitosMinimosService requisitosService;

    public RequisitosMinimosController(RequisitosMinimosService requisitosService) {
        this.requisitosService = requisitosService;
    }

    @Operation(
            summary = "Buscar requisitos mínimos do jogo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping
    public ResponseEntity<RequisitosMinimos> buscarPorJogoId(@PathVariable("id") Long jogoId) {
        return ResponseEntity.ok(requisitosService.buscarPorJogoId(jogoId));
    }

    @Operation(
            summary = "Cadastrar requisitos mínimos do jogo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "processador": "Intel Core i5-2500K 3.3 GHz",
                              "memoria": "8 GB RAM",
                              "placaDeVideo": "NVIDIA GeForce GTX 660",
                              "sistemaOperacional": "Windows 10 64-bit"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou jogo já possui requisitos"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @PostMapping
    public ResponseEntity<RequisitosMinimos> cadastrar(@PathVariable("id") Long jogoId,
                                                       @Valid @RequestBody RequisitosMinimos requisitos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requisitosService.salvar(jogoId, requisitos));
    }

    @Operation(
            summary = "Atualizar requisitos mínimos do jogo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "processador": "Intel Core i5-2500K 3.3 GHz",
                              "memoria": "8 GB RAM",
                              "placaDeVideo": "NVIDIA GeForce GTX 660",
                              "sistemaOperacional": "Windows 10 64-bit"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Requisitos atualizados"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PutMapping
    public ResponseEntity<RequisitosMinimos> atualizar(@PathVariable("id") Long jogoId,
                                                       @Valid @RequestBody RequisitosMinimos requisitos) {
        return ResponseEntity.ok(requisitosService.atualizar(jogoId, requisitos));
    }

    @Operation(
            summary = "Excluir requisitos mínimos do jogo",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @DeleteMapping
    public ResponseEntity<Void> deletar(@PathVariable("id") Long jogoId) {
        requisitosService.deletarPorJogoId(jogoId);
        return ResponseEntity.noContent().build();
    }
}
