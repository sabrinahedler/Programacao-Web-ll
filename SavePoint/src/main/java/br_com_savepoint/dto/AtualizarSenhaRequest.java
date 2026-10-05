package br_com_savepoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo da requisição de troca de senha. */
public record AtualizarSenhaRequest(@NotBlank(message = "novaSenha é obrigatória") @Size(min = 6, message = "novaSenha deve ter no mínimo 6 caracteres") String novaSenha) {
}
