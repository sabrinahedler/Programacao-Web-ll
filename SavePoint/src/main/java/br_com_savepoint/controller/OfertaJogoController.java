package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.OfertaJogo;
import br_com_savepoint.service.OfertaJogoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Ofertas", description = "Preços dos jogos em cada loja")
@RestController
public class OfertaJogoController {

    private final OfertaJogoService ofertaService;

    public OfertaJogoController(OfertaJogoService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @Operation(
            summary = "Cadastrar oferta de um jogo em uma loja",
            description = "O percentual de desconto é calculado a partir dos preços original e atual.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "precoOriginal": 199.9,
                              "precoAtual": 99.9,
                              "loja": { "id": 1 }
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Jogo ou loja não encontrado")
            })
    @PostMapping("/jogos/{id}/ofertas")
    public ResponseEntity<OfertaJogo> cadastrarOferta(@PathVariable("id") Long jogoId, @Valid @RequestBody OfertaJogo oferta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ofertaService.cadastrarOferta(jogoId, oferta));
    }

    @Operation(
            summary = "Comparar preços do jogo entre lojas",
            description = "O parâmetro de caminho é o ID do jogo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ofertas ordenadas do menor para o maior preço atual"),
                    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
            })
    @GetMapping("/ofertas/{id}/comparacao")
    public ResponseEntity<List<OfertaJogo>> compararPrecos(@PathVariable("id") Long jogoId) {
        return ResponseEntity.ok(ofertaService.compararPrecosPorJogo(jogoId));
    }

    @Operation(
            summary = "Buscar oferta por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/ofertas/{id}")
    public ResponseEntity<OfertaJogo> buscarPorId(@PathVariable("id") Long ofertaId) {
        return ResponseEntity.ok(ofertaService.buscarPorId(ofertaId));
    }
}
