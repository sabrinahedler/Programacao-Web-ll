package br_com_savepoint.model;

import java.util.List;

// se nickname está disponível e se não estiver, 3 sugestões
public record NomeDisponivelDTO(boolean disponivel, List<String> sugestoes) {
}