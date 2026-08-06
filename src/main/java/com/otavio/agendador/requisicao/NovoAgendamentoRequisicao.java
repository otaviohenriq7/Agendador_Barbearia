package com.otavio.agendador.requisicao;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

// "record" cria uma classe imutável só com os dados, sem precisar escrever
// getters/setters/construtor na mão — ideal pra um objeto que só carrega dados.
public record NovoAgendamentoRequisicao(

        @NotNull(message = "O cliente é obrigatório")
        Long clienteId,

        @NotNull(message = "O profissional é obrigatório")
        Long profissionalId,

        @NotNull(message = "O serviço é obrigatório")
        Long servicoId,

        @NotNull(message = "O horário de início é obrigatório")
        LocalDateTime inicio
) {
}
