const rotas = {
    "#/agendamentos": telaAgendamentos,
    "#/clientes": telaClientes,
    "#/profissionais": telaProfissionais,
    "#/servicos": telaServicos
};

const ROTA_PADRAO = "#/agendamentos";

let telaAtual = null;
let idEmEdicao = null;

async function rotear() {
    telaAtual = null;
    idEmEdicao = null;

    if (!lerToken()) {
        mostrarMenu(false);
        telaLogin();
        return;
    }

    mostrarMenu(true);

    const rotaAtual = rotas[window.location.hash] ? window.location.hash : ROTA_PADRAO;

    document.querySelectorAll("nav a").forEach(function (link) {
        link.classList.toggle("ativo", link.getAttribute("href") === rotaAtual);
    });

    const temporizadorDeCarregamento = setTimeout(function () {
        desenhar(`<p class="carregando">Carregando...</p>`);
    }, 200);

    try {
        await rotas[rotaAtual]();
    } catch (erro) {
        mostrarAviso(erro.message, "erro");
        if (erro.naoAutenticado) {
            await rotear();
        }
    } finally {
        clearTimeout(temporizadorDeCarregamento);
    }
}

function mostrarMenu(visivel) {
    document.getElementById("menu").classList.toggle("escondido", !visivel);
    document.getElementById("sair").classList.toggle("escondido", !visivel);
}

function telaLogin() {
    desenhar(`
        <div class="login">
            <h2>Entrar</h2>
            <div class="cartao">
                <form id="formulario">
                    <div class="campo">
                        <label for="login">Login</label>
                        <input id="login" name="login" required>
                    </div>
                    <div class="campo">
                        <label for="senha">Senha</label>
                        <input id="senha" name="senha" type="password" required>
                    </div>
                    <button type="submit">Entrar</button>
                </form>
            </div>
        </div>
    `);

    document.getElementById("formulario").addEventListener("submit", async function (evento) {
        evento.preventDefault();

        const dados = Object.fromEntries(new FormData(evento.target));

        try {
            await entrar(dados.login, dados.senha);
            await rotear();
        } catch (erro) {
            mostrarAviso(erro.message, "erro");
        }
    });
}

document.getElementById("sair").addEventListener("click", function () {
    descartarToken();
    rotear();
});

window.addEventListener("hashchange", rotear);
window.addEventListener("load", rotear);

async function telaClientes() {
    const clientes = await listar("clientes");

    desenhar(`
        <h2>Clientes</h2>
        <div class="cartao">
            <form id="formulario">
                <div class="campo">
                    <label for="nome">Nome</label>
                    <input id="nome" name="nome" required>
                </div>
                <div class="campo">
                    <label for="telefone">Telefone</label>
                    <input id="telefone" name="telefone" required>
                </div>
                <div class="campo">
                    <label for="email">E-mail</label>
                    <input id="email" name="email" type="email">
                </div>
                <button type="submit">Cadastrar</button>
            </form>
        </div>
        <div class="cartao">
            ${montarTabela(
                ["Nome", "Telefone", "E-mail"],
                clientes,
                function (cliente) {
                    return [cliente.nome, formatarTelefone(cliente.telefone), cliente.email || "-"];
                }
            )}
        </div>
    `);

    configurarTela({
        recurso: "clientes",
        itens: clientes,
        montarDados: function (dados) {
            return dados;
        },
        preencher: function (cliente) {
            const formulario = document.getElementById("formulario");
            formulario.nome.value = cliente.nome;
            formulario.telefone.value = cliente.telefone;
            formulario.email.value = cliente.email || "";
        }
    });
}

async function telaProfissionais() {
    const profissionais = await listar("profissionais");

    desenhar(`
        <h2>Profissionais</h2>
        <div class="cartao">
            <form id="formulario">
                <div class="campo">
                    <label for="nome">Nome</label>
                    <input id="nome" name="nome" required>
                </div>
                <div class="campo">
                    <label for="telefone">Telefone</label>
                    <input id="telefone" name="telefone" required>
                </div>
                <button type="submit">Cadastrar</button>
            </form>
        </div>
        <div class="cartao">
            ${montarTabela(
                ["Nome", "Telefone"],
                profissionais,
                function (profissional) {
                    return [profissional.nome, formatarTelefone(profissional.telefone)];
                }
            )}
        </div>
    `);

    configurarTela({
        recurso: "profissionais",
        itens: profissionais,
        montarDados: function (dados) {
            return dados;
        },
        preencher: function (profissional) {
            const formulario = document.getElementById("formulario");
            formulario.nome.value = profissional.nome;
            formulario.telefone.value = profissional.telefone;
        }
    });
}

async function telaServicos() {
    const servicos = await listar("servicos");

    desenhar(`
        <h2>Serviços</h2>
        <div class="cartao">
            <form id="formulario">
                <div class="campo">
                    <label for="nome">Nome</label>
                    <input id="nome" name="nome" required>
                </div>
                <div class="campo">
                    <label for="duracaoEmMinutos">Duração (minutos)</label>
                    <input id="duracaoEmMinutos" name="duracaoEmMinutos" type="number" min="1" required>
                </div>
                <button type="submit">Cadastrar</button>
            </form>
        </div>
        <div class="cartao">
            ${montarTabela(
                ["Nome", "Duração"],
                servicos,
                function (servico) {
                    return [servico.nome, servico.duracaoEmMinutos + " min"];
                }
            )}
        </div>
    `);

    configurarTela({
        recurso: "servicos",
        itens: servicos,
        montarDados: function (dados) {
            dados.duracaoEmMinutos = Number(dados.duracaoEmMinutos);
            return dados;
        },
        preencher: function (servico) {
            const formulario = document.getElementById("formulario");
            formulario.nome.value = servico.nome;
            formulario.duracaoEmMinutos.value = servico.duracaoEmMinutos;
        }
    });
}

async function telaAgendamentos() {
    const [agendamentos, clientes, profissionais, servicos] = await Promise.all([
        listar("agendamentos"),
        listar("clientes"),
        listar("profissionais"),
        listar("servicos")
    ]);

    if (clientes.length === 0 || profissionais.length === 0 || servicos.length === 0) {
        desenhar(`
            <h2>Agendamentos</h2>
            <div class="cartao">
                <p class="vazio">
                    Para agendar é preciso ter ao menos um cliente, um profissional
                    e um serviço cadastrados.
                </p>
            </div>
        `);
        return;
    }

    desenhar(`
        <h2>Agendamentos</h2>
        <div class="cartao">
            <form id="formulario">
                <div class="campo">
                    <label for="clienteId">Cliente</label>
                    <select id="clienteId" name="clienteId">${montarOpcoes(clientes)}</select>
                </div>
                <div class="campo">
                    <label for="profissionalId">Profissional</label>
                    <select id="profissionalId" name="profissionalId">${montarOpcoes(profissionais)}</select>
                </div>
                <div class="campo">
                    <label for="servicoId">Serviço</label>
                    <select id="servicoId" name="servicoId">${montarOpcoes(servicos)}</select>
                </div>
                <div class="campo">
                    <label for="inicio">Início</label>
                    <input id="inicio" name="inicio" type="datetime-local" required>
                </div>
                <button type="submit">Agendar</button>
            </form>
        </div>
        <div class="cartao">
            ${montarTabela(
                ["Cliente", "Profissional", "Serviço", "Início", "Fim"],
                agendamentos,
                function (agendamento) {
                    return [
                        agendamento.cliente.nome,
                        agendamento.profissional.nome,
                        agendamento.servico.nome,
                        formatarDataHora(agendamento.inicio),
                        formatarDataHora(agendamento.fim)
                    ];
                }
            )}
        </div>
    `);

    configurarTela({
        recurso: "agendamentos",
        itens: agendamentos,
        montarDados: function (dados) {
            return {
                clienteId: Number(dados.clienteId),
                profissionalId: Number(dados.profissionalId),
                servicoId: Number(dados.servicoId),
                inicio: dados.inicio
            };
        },
        preencher: function (agendamento) {
            const formulario = document.getElementById("formulario");
            formulario.clienteId.value = agendamento.cliente.id;
            formulario.profissionalId.value = agendamento.profissional.id;
            formulario.servicoId.value = agendamento.servico.id;
            formulario.inicio.value = agendamento.inicio.slice(0, 16);
        }
    });
}

function desenhar(html) {
    document.getElementById("conteudo").innerHTML = html;
}

function escapar(valor) {
    return String(valor)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function montarOpcoes(itens) {
    return itens
        .map(function (item) {
            return `<option value="${item.id}">${escapar(item.nome)}</option>`;
        })
        .join("");
}

function montarTabela(colunas, itens, extrairCelulas) {
    if (itens.length === 0) {
        return `<p class="vazio">Nada cadastrado ainda.</p>`;
    }

    const cabecalho = colunas
        .map(function (coluna) {
            return `<th>${coluna}</th>`;
        })
        .join("");

    const linhas = itens
        .map(function (item) {
            const celulas = extrairCelulas(item)
                .map(function (celula) {
                    return `<td>${escapar(celula)}</td>`;
                })
                .join("");

            return `
                <tr>
                    ${celulas}
                    <td class="acoes">
                        <button class="editar" data-id="${item.id}">editar</button>
                        <button class="remover" data-id="${item.id}">remover</button>
                    </td>
                </tr>
            `;
        })
        .join("");

    return `<table><thead><tr>${cabecalho}<th></th></tr></thead><tbody>${linhas}</tbody></table>`;
}

function configurarTela(configuracao) {
    telaAtual = configuracao;

    const formulario = document.getElementById("formulario");
    if (!formulario) {
        return;
    }

    formulario.addEventListener("submit", async function (evento) {
        evento.preventDefault();

        const dados = telaAtual.montarDados(Object.fromEntries(new FormData(formulario)));

        try {
            if (idEmEdicao === null) {
                await criar(telaAtual.recurso, dados);
                mostrarAviso("Salvo com sucesso", "sucesso");
            } else {
                await atualizar(telaAtual.recurso, idEmEdicao, dados);
                mostrarAviso("Alteração salva", "sucesso");
            }
            await rotear();
        } catch (erro) {
            mostrarAviso(erro.message, "erro");
            if (erro.naoAutenticado) {
                await rotear();
            }
        }
    });
}

function entrarEmModoEdicao(item) {
    idEmEdicao = item.id;
    telaAtual.preencher(item);

    const botaoSalvar = document.querySelector("#formulario button[type=submit]");
    botaoSalvar.textContent = "Salvar alteração";

    if (!document.getElementById("cancelar")) {
        const botaoCancelar = document.createElement("button");
        botaoCancelar.id = "cancelar";
        botaoCancelar.type = "button";
        botaoCancelar.className = "secundario";
        botaoCancelar.textContent = "Cancelar";
        botaoCancelar.addEventListener("click", rotear);
        botaoSalvar.insertAdjacentElement("afterend", botaoCancelar);
    }

    document.getElementById("formulario").scrollIntoView({ behavior: "smooth", block: "center" });
}

document.addEventListener("click", function (evento) {
    const botao = evento.target.closest("button.editar");
    if (!botao || !telaAtual) {
        return;
    }

    const item = telaAtual.itens.find(function (candidato) {
        return String(candidato.id) === botao.dataset.id;
    });

    if (item) {
        entrarEmModoEdicao(item);
    }
});

function confirmar(texto) {
    return new Promise(function (resolver) {
        const caixa = document.getElementById("confirmacao");
        const sim = document.getElementById("confirmacao-sim");
        const nao = document.getElementById("confirmacao-nao");

        document.getElementById("confirmacao-texto").textContent = texto;
        caixa.classList.remove("escondido");
        sim.focus();

        function encerrar(resposta) {
            caixa.classList.add("escondido");
            sim.removeEventListener("click", aoConfirmar);
            nao.removeEventListener("click", aoCancelar);
            document.removeEventListener("keydown", aoTeclar);
            resolver(resposta);
        }

        function aoConfirmar() {
            encerrar(true);
        }

        function aoCancelar() {
            encerrar(false);
        }

        function aoTeclar(evento) {
            if (evento.key === "Escape") {
                encerrar(false);
            }
        }

        sim.addEventListener("click", aoConfirmar);
        nao.addEventListener("click", aoCancelar);
        document.addEventListener("keydown", aoTeclar);
    });
}

document.addEventListener("click", async function (evento) {
    const botao = evento.target.closest("button.remover");
    if (!botao || !telaAtual) {
        return;
    }

    const linha = botao.closest("tr");
    const descricao = linha ? linha.querySelector("td").textContent.trim() : "este registro";

    const confirmado = await confirmar(`Remover "${descricao}"? Essa ação não pode ser desfeita.`);
    if (!confirmado) {
        return;
    }

    try {
        await remover(telaAtual.recurso, botao.dataset.id);
        mostrarAviso("Removido", "sucesso");
        await rotear();
    } catch (erro) {
        mostrarAviso(erro.message, "erro");
        if (erro.naoAutenticado) {
            await rotear();
        }
    }
});

let temporizadorDoAviso = null;

function mostrarAviso(texto, tipo) {
    const aviso = document.getElementById("aviso");
    aviso.textContent = texto;
    aviso.className = "aviso " + tipo;

    clearTimeout(temporizadorDoAviso);
    temporizadorDoAviso = setTimeout(function () {
        aviso.classList.add("escondido");
    }, 3500);
}

function formatarTelefone(valor) {
    const digitos = String(valor).replace(/\D/g, "");

    if (digitos.length === 11) {
        return `(${digitos.slice(0, 2)}) ${digitos.slice(2, 7)}-${digitos.slice(7)}`;
    }

    if (digitos.length === 10) {
        return `(${digitos.slice(0, 2)}) ${digitos.slice(2, 6)}-${digitos.slice(6)}`;
    }

    return valor;
}

function formatarDataHora(textoIso) {
    const data = new Date(textoIso);
    return data.toLocaleString("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}
