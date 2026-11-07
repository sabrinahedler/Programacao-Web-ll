//
const API_BASE_URL = "";

const TOKEN_STORAGE_KEY = "savepoint_jwt_token";

function salvarToken(token) {
    if (token) {
        localStorage.setItem(TOKEN_STORAGE_KEY, token);
    }
}

function obterToken() {
    return localStorage.getItem(TOKEN_STORAGE_KEY);
}

function limparSessao() {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
}

function estaLogado() {
    return !!obterToken();
}

// guarda o status HTTP e a mensagem que o back-end retornou
class ApiError extends Error {
    constructor(status, mensagem) {
        super(mensagem);
        this.status = status;
    }
}

function obterMensagemErroPadrao(status) {
    switch (status) {
        case 400:
            return "Requisição inválida. Verifique os dados preenchidos.";
        case 401:
            return "Credenciais inválidas ou sessão expirada.";
        case 403:
            return "Acesso negado. Você não tem permissão para esta ação.";
        case 404:
            return "Recurso não encontrado.";
        case 500:
            return "Erro interno no servidor. Tente novamente mais tarde.";
        default:
            return "Ocorreu um erro ao processar a requisição.";
    }
}

// função central
async function apiRequest(endpoint, { method = "GET", body = null, autenticado = false } = {}) {
    const headers = {};

    if (body) {
        headers["Content-Type"] = "application/json";
    }

    if (autenticado) {
        const token = obterToken();
        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }
    }

    let resposta;

    try {
        resposta = await fetch(API_BASE_URL + endpoint, {
            method,
            headers,
            body: body ? JSON.stringify(body) : null,
        });
    } catch (err) {
        throw new ApiError(0, "Não foi possível conectar ao servidor. Verifique sua conexão.");
    }

    const textoResposta = await resposta.text();
    let dados = null;

    if (textoResposta) {
        try {
            dados = JSON.parse(textoResposta);
        } catch {
            dados = textoResposta;
        }
    }

    if (!resposta.ok) {
        if (resposta.status === 401 && autenticado) {
            limparSessao();
        }

        let mensagemErro = obterMensagemErroPadrao(resposta.status);

        if (dados) {
            if (typeof dados === "string") {
                mensagemErro = dados;
            } else if (dados.mensagem) {
                mensagemErro = dados.mensagem;
            } else if (dados.erro) {
                mensagemErro = dados.erro;
            }
        }
        throw new ApiError(resposta.status, mensagemErro);
    }
    return dados;
}

// endpoints usados
const SavePointApi = {
    login: async (email, senha) => {
        const dados = await apiRequest("/auth/login", {
            method: "POST",
            body: { email, senha },
        });
        if (dados && dados.token) {
            salvarToken(dados.token);
        }
        return dados;
    },

    cadastrar: (nome, email, senha) =>
        apiRequest("/auth/register", {
            method: "POST",
            body: { nome, email, senha, role: "USER" },
        }),

    listarJogos: () => apiRequest("/jogos"),

    buscarJogoPorId: (id) => apiRequest(`/jogos/${id}`),

    compararOfertas: (jogoId) =>
        apiRequest(`/ofertas/${jogoId}/comparacao`, { autenticado: true }),

    logout: () => {
        limparSessao();
        window.location.href = "/login.html";
    }
};