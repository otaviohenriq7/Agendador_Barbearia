package com.otavio.agendador.requisicao;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

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
