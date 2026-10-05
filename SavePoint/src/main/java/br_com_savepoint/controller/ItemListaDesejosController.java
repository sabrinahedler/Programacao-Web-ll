package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.ItemListaDesejos;
import br_com_savepoint.service.ItemListaDesejosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Itens da lista de desejos", description = "Jogos da lista de desejos com alerta de preço")
@RestController
@RequestMapping("/usuarios/{id}/lista-desejos/itens")
public class ItemListaDesejosController {

    private final ItemListaDesejosService itemService;

    public ItemListaDesejosController(ItemListaDesejosService itemService) {
        this.itemService = itemService;
    }

    @Operation(
            summary = "Adicionar jogo à lista de desejos",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "precoAlerta": 79.9,
                              "notificarOferta": true,
                              "jogo": { "id": 1 }
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou jogo já está na lista"),
                    @ApiResponse(responseCode = "404", description = "Lista ou jogo não encontrado")
            })
    @PostMapping
    public ResponseEntity<ItemListaDesejos> adicionarItem(@PathVariable("id") Long usuarioId,
                                                          @Valid @RequestBody ItemListaDesejos item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.adicionarItem(usuarioId, item));
    }

    @Operation(
            summary = "Buscar item da lista de desejos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemListaDesejos> buscarItem(@PathVariable("id") Long usuarioId,
                                                       @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.buscarItem(usuarioId, itemId));
    }

    @Operation(
            summary = "Atualizar item da lista de desejos",
            description = "Atualiza o preço de alerta e a notificação de oferta.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "precoAlerta": 79.9,
                              "notificarOferta": true,
                              "jogo": { "id": 1 }
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item atualizado"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PutMapping("/{itemId}")
    public ResponseEntity<ItemListaDesejos> atualizarItem(@PathVariable("id") Long usuarioId,
                                                          @PathVariable Long itemId,
                                                          @Valid @RequestBody ItemListaDesejos item) {
        return ResponseEntity.ok(itemService.atualizarItem(usuarioId, itemId, item));
    }
}
