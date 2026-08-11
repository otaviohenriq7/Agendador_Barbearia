const CHAVE_DO_TOKEN = "agendador.token";

function lerToken() {
    return localStorage.getItem(CHAVE_DO_TOKEN);
}

function guardarToken(token) {
    localStorage.setItem(CHAVE_DO_TOKEN, token);
}

function descartarToken() {
    localStorage.removeItem(CHAVE_DO_TOKEN);
}

function cabecalhos(temCorpo) {
    const montados = {};

    if (temCorpo) {
        montados["Content-Type"] = "application/json";
    }

    const token = lerToken();
    if (token) {
        montados["Authorization"] = "Bearer " + token;
    }

    return montados;
}

async function lerResposta(resposta) {
    if (resposta.ok) {
        if (resposta.status === 204) {
            return null;
        }
        return resposta.json();
    }

    if (resposta.status === 401) {
        descartarToken();
    }

    const corpo = await resposta.json().catch(() => null);
    const erro = new Error(extrairMensagemDeErro(corpo));
    erro.naoAutenticado = resposta.status === 401;
    throw erro;
}

function extrairMensagemDeErro(corpo) {
    if (!corpo) {
        return "Erro inesperado na requisição";
    }

    if (Array.isArray(corpo.errors) && corpo.errors.length > 0) {
        return corpo.errors
            .map(function (erro) {
                return erro.defaultMessage;
            })
            .join(". ");
    }

    return corpo.message || "Erro inesperado na requisição";
}

async function entrar(login, senha) {
    const resposta = await fetch("/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login: login, senha: senha })
    });

    const corpo = await lerResposta(resposta);
    guardarToken(corpo.token);
}

async function listar(recurso) {
    const resposta = await fetch("/" + recurso, { headers: cabecalhos(false) });
    return lerResposta(resposta);
}

async function criar(recurso, dados) {
    const resposta = await fetch("/" + recurso, {
        method: "POST",
        headers: cabecalhos(true),
        body: JSON.stringify(dados)
    });
    return lerResposta(resposta);
}

async function atualizar(recurso, id, dados) {
    const resposta = await fetch("/" + recurso + "/" + id, {
        method: "PUT",
        headers: cabecalhos(true),
        body: JSON.stringify(dados)
    });
    return lerResposta(resposta);
}

async function remover(recurso, id) {
    const resposta = await fetch("/" + recurso + "/" + id, {
        method: "DELETE",
        headers: cabecalhos(false)
    });
    return lerResposta(resposta);
}
