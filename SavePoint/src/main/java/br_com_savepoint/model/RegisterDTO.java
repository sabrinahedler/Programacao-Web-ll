package br_com_savepoint.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de cadastro público. A role NÃO vem do cliente: todo cadastro público nasce como USER.
 * O administrador inicial é criado pelas variáveis ADMIN_EMAIL / ADMIN_PASSWORD (ver AdminSeeder).
 */
public record RegisterDTO(
        @NotBlank(message = "nome é obrigatório") String nome,
        @NotBlank(message = "email é obrigatório") @Email(message = "email inválido") String email,
        @NotBlank(message = "senha é obrigatória") @Size(min = 6, max = 72, message = "senha deve ter entre 6 e 72 caracteres") String senha) {
}
