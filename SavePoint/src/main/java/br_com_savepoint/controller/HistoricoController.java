package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.Historico;
import br_com_savepoint.service.HistoricoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Histórico de preços", description = "Evolução do preço de cada oferta")
@RestController
@RequestMapping("/ofertas")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @Operation(
            summary = "Listar histórico de preços da oferta",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Histórico do mais recente para o mais antigo"),
                    @ApiResponse(responseCode = "404", description = "Oferta não encontrada")
            })
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<Historico>> buscarPorOfertaId(@PathVariable("id") Long ofertaId) {
        return ResponseEntity.ok(historicoService.buscarPorOfertaId(ofertaId));
    }

    @Operation(
            summary = "Registrar alteração de preço da oferta",
            description = "Registra o novo preço no histórico, atualiza o preço atual da oferta e recalcula o desconto.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "preco": 79.9
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Oferta não encontrada")
            })
    @PostMapping("/{id}/historico")
    public ResponseEntity<Historico> registrarAlteracaoPreco(@PathVariable("id") Long ofertaId,
                                                             @Valid @RequestBody Historico historico) {
        Historico registrado = historicoService.registrarAlteracaoPreco(ofertaId, historico.getPreco());
        return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
    }
}
