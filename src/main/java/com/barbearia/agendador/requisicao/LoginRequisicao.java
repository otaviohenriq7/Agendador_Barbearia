package com.barbearia.agendador.requisicao;

import jakarta.validation.constraints.NotBlank;

public record LoginRequisicao(

        @NotBlank(message = "O login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}
