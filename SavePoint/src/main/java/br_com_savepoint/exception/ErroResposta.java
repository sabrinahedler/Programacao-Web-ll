package br_com_savepoint.exception;

import java.time.LocalDateTime;

/** Corpo padrão das respostas de erro da API. */
public record ErroResposta(LocalDateTime timestamp, int status, String erro, String mensagem) {
}