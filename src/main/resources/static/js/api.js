async function lerResposta(resposta) {
    if (resposta.ok) {
        if (resposta.status === 204) {
            return null;
        }
        return resposta.json();
    }

    const corpo = await resposta.json().catch(() => null);
    throw new Error(extrairMensagemDeErro(corpo));
}

function extrairMensagemDeErro(corpo) {
    if (!corpo) {
        return "Erro inesperado na requisicao";
    }

    if (Array.isArray(corpo.errors) && corpo.errors.length > 0) {
        return corpo.errors
            .map(function (erro) {
                return erro.defaultMessage;
            })
            .join(". ");
    }

    return corpo.message || "Erro inesperado na requisicao";
}

async function listar(recurso) {
    const resposta = await fetch("/" + recurso);
    return lerResposta(resposta);
}

async function criar(recurso, dados) {
    const resposta = await fetch("/" + recurso, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dados)
    });
    return lerResposta(resposta);
}

async function atualizar(recurso, id, dados) {
    const resposta = await fetch("/" + recurso + "/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dados)
    });
    return lerResposta(resposta);
}

async function remover(recurso, id) {
    const resposta = await fetch("/" + recurso + "/" + id, { method: "DELETE" });
    return lerResposta(resposta);
}
