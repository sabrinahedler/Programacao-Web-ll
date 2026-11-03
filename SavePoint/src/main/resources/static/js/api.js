// quando colocar a aplicação no banco de dados, inserir o endereço do banco
const API_BASE_URL = "http://localhost:8080";

const SESSION_KEY = "savepoint.sessao";

// guarda o retorno do POST /auth/login
function salvarSessao(respostaLogin) {
    const sessao = {
        token: respostaLogin.token,
        tipo: respostaLogin.tipo,
        role: respostaLogin.role,
        expiraEm: Date.now() + respostaLogin.expiraEmSegundos * 1000,
    };

    try {
        localStorage.setItem(SESSION_KEY, JSON.stringify(sessao));
    } catch {
        // segue sem sessão salva
    }
}

// retorna sessão salva, ou null se não existir/token já expirou
function obterSessao() {
    try {
        const sessao = JSON.parse(localStorage.getItem(SESSION_KEY));

        if (!sessao || !sessao.token) {
            return null;
        }

        if (Date.now() >= sessao.expiraEm) {
            limparSessao();
            return null;
        }

        return sessao;
    } catch {
        return null;
    }
}

function limparSessao() {
    try {
        localStorage.removeItem(SESSION_KEY);
    } catch {
        // nada a fazer
    }
}

function estaLogado() {
    return obterSessao() !== null;
}

// guarda o status HTTP e a mensagem que o back-end retornou
class ApiError extends Error {
    constructor(status, mensagem) {
        super(mensagem);
        this.status = status;
    }
}

function mensagemPadrao(status) {
    if (status === 401) return "Sua sessão expirou. Entre novamente.";
    if (status === 403) return "Você não tem permissão para esta ação.";
    if (status === 404) return "Recurso não encontrado.";
    if (status >= 500) return "Erro no servidor. Tente novamente em instantes.";
    return "Não foi possível concluir a requisição.";
}

// função central
async function apiRequest(caminho, { method = "GET", body, autenticado = false } = {}) {
    const headers = {};

    if (body !== undefined) {
        headers["Content-Type"] = "application/json";
    }

    if (autenticado) {
        const sessao = obterSessao();
        if (sessao) {
            headers["Authorization"] = `${sessao.tipo} ${sessao.token}`;
        }
    }

    let resposta;

    try {
        resposta = await fetch(API_BASE_URL + caminho, {
            method,
            headers,
            body: body !== undefined ? JSON.stringify(body) : undefined,
        });
    } catch {
        throw new ApiError(0, "Não foi possível conectar ao servidor. Verifique se o back-end está rodando.");
    }

    const texto = await resposta.text();
    let dados = null;

    if (texto) {
        try {
            dados = JSON.parse(texto);
        } catch {
            dados = texto;
        }
    }

    if (!resposta.ok) {
        // token inválido: descarta a sessão para o site voltar ao modo visitante
        if (resposta.status === 401 && autenticado) {
            limparSessao();
        }

        const mensagem =
            (dados && dados.mensagem) ||
            (typeof dados === "string" && dados) ||
            mensagemPadrao(resposta.status);

        throw new ApiError(resposta.status, mensagem);
    }

    return dados;
}

// endpoints usados
const SavePointApi = {
    // POST /auth/login  ->  { token, tipo, expiraEmSegundos, role }
    login: (email, senha) =>
        apiRequest("/auth/login", { method: "POST", body: { email, senha } }),

    // POST /auth/register  ->  201 sem corpo
    cadastrar: (nome, email, senha) =>
        apiRequest("/auth/register", { method: "POST", body: { nome, email, senha } }),

    // GET /jogos (público)  ->  lista de jogos
    listarJogos: () => apiRequest("/jogos"),

    // GET /ofertas/{jogoId}/comparacao (exige login)  ->  ofertas do jogo, da mais barata para a mais cara
    compararOfertas: (jogoId) =>
        apiRequest(`/ofertas/${jogoId}/comparacao`, { autenticado: true }),
};