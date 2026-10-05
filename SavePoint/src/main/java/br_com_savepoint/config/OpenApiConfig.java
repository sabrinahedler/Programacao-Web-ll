package br_com_savepoint.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação OpenAPI (Swagger).
 * Interface: /swagger-ui.html | Documento JSON: /v3/api-docs
 */
@Configuration
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic", in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(
        info = @Info(
                title = "SavePoint API",
                version = "1.0.0",
                description = "API REST do SavePoint: catálogo de jogos, comparação de preços entre lojas, "
                        + "histórico de preços, avaliações de usuários e lista de desejos."
        ),
        security = @SecurityRequirement(name = "basicAuth"),
        servers = @Server(url = "http://localhost:8080", description = "Ambiente local"),
        tags = {
                @Tag(name = "Jogos", description = "Cadastro e consulta de jogos"),
                @Tag(name = "Lojas", description = "Lojas onde os jogos são vendidos"),
                @Tag(name = "Ofertas", description = "Preços dos jogos em cada loja"),
                @Tag(name = "Histórico de preços", description = "Evolução do preço de cada oferta"),
                @Tag(name = "Requisitos mínimos", description = "Requisitos mínimos de hardware de cada jogo"),
                @Tag(name = "Avaliações", description = "Avaliações dos usuários sobre os jogos"),
                @Tag(name = "Resumo de avaliações", description = "Resumo consolidado das avaliações de um jogo"),
                @Tag(name = "Usuários", description = "Cadastro e gestão de usuários"),
                @Tag(name = "Lista de desejos", description = "Lista de desejos do usuário"),
                @Tag(name = "Itens da lista de desejos", description = "Jogos da lista de desejos com alerta de preço")
        }
)
public class OpenApiConfig {
}
