package br_com_savepoint.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br_com_savepoint.model.Loja;
import br_com_savepoint.service.LojaService;
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

@Tag(name = "Lojas", description = "Lojas onde os jogos são vendidos")
@RestController
@RequestMapping("/lojas")
public class LojaController {

    private final LojaService lojaService;

    public LojaController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @Operation(
            summary = "Listar todas as lojas",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de lojas")
            })
    @GetMapping
    public ResponseEntity<List<Loja>> listarTodas() {
        return ResponseEntity.ok(lojaService.listarTodas());
    }

    @Operation(
            summary = "Buscar loja por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Loja> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(lojaService.buscarPorId(id));
    }

    @Operation(
            summary = "Cadastrar loja",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "nome": "Steam",
                              "urlLoja": "https://store.steampowered.com",
                              "urlLogo": "https://exemplo.com/logos/steam.png"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já cadastrado")
            })
    @PostMapping
    public ResponseEntity<Loja> salvar(@Valid @RequestBody Loja loja) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lojaService.salvar(loja));
    }

    @Operation(
            summary = "Atualizar loja",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "nome": "Steam",
                              "urlLoja": "https://store.steampowered.com",
                              "urlLogo": "https://exemplo.com/logos/steam.png"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Loja atualizada"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já cadastrado"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @PutMapping("/{id}")
    public ResponseEntity<Loja> atualizar(@PathVariable Long id, @Valid @RequestBody Loja loja) {
        return ResponseEntity.ok(lojaService.atualizar(id, loja));
    }

    @Operation(
            summary = "Excluir loja",
            description = "Remove também as ofertas da loja e o histórico delas.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido com sucesso (sem conteúdo)"),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        lojaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
