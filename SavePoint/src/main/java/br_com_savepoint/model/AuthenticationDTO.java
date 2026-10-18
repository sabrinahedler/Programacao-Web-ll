package br_com_savepoint.model;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank(message = "email é obrigatório") String email,
        @NotBlank(message = "senha é obrigatória") String senha) {
}
