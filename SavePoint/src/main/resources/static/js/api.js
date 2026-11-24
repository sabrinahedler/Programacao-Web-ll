//   api.js — toda a comunicação com o back-end passa por aqui
const API_BASE_URL = "";

const CHAVE_SESSAO = "savepoint.sessao";
const CHAVE_USUARIO = "savepoint.usuario";

// navegador pode bloquear o localStorage
function lerArmazenado(chave) {
  try {
    return JSON.parse(localStorage.getItem(chave));
  } catch {
    return null;
  }
}

function gravarArmazenado(chave, valor) {
  try {
    localStorage.setItem(chave, JSON.stringify(valor));
  } catch {
    // aba anônima ou armazenamento bloqueado, o site segue funcionando sem lembrar o login
  }
}

function apagarArmazenado(chave) {
  try {
    localStorage.removeItem(chave);
  } catch {
    // nada a fazer
  }
}

// token JWT

function lerPayloadJwt(token) {
  try {
    const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    const json = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + c.charCodeAt(0).toString(16).padStart(2, "0"))
        .join("")
    );
    return JSON.parse(json);
  } catch {
    return null;
  }
}

// true se o token vence em menos de "margemSegundos"
function tokenVencido(token, margemSegundos = 30) {
  const payload = token ? lerPayloadJwt(token) : null;
  if (!payload || !payload.exp) return true;
  return Date.now() / 1000 >= payload.exp - margemSegundos;
}

// sessão
function salvarSessao({ token, refreshToken }) {
  gravarArmazenado(CHAVE_SESSAO, { token, refreshToken });
}

function obterSessao() {
  const sessao = lerArmazenado(CHAVE_SESSAO);
  if (!sessao || !sessao.token || !sessao.refreshToken) return null;

  // refresh token dura 7 dias / vencido = precisa entrar de novo
  if (tokenVencido(sessao.refreshToken, 0)) {
    limparSessao();
    return null;
  }
  return sessao;
}

function limparSessao() {
  apagarArmazenado(CHAVE_SESSAO);
  apagarArmazenado(CHAVE_USUARIO);
}

function estaLogado() {
  return obterSessao() !== null;
}

// o token de acesso dura 5 min
// esta função devolve um token válido, renovando com o refresh token se precisar
let renovacaoEmAndamento = null;

async function obterTokenValido() {
  const sessao = obterSessao();
  if (!sessao) return null;
  if (!tokenVencido(sessao.token)) return sessao.token;

  // se várias chamadas pedirem renovação ao mesmo tempo, todas esperam a mesma
  if (!renovacaoEmAndamento) {
    renovacaoEmAndamento = (async () => {
      try {
        const resposta = await fetch(API_BASE_URL + "/auth/refresh", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ refreshToken: sessao.refreshToken }),
        });
        if (!resposta.ok) throw new Error("refresh recusado");
        const dados = await resposta.json();
        salvarSessao(dados);
        return dados.token;
      } catch {
        limparSessao();
        return null;
      } finally {
        renovacaoEmAndamento = null;
      }
    })();
  }
  return renovacaoEmAndamento;
}

// requisições
class ApiError extends Error {
  constructor(status, mensagem) {
    super(mensagem);
    this.status = status;
  }
}

function mensagemPadrao(status) {
  if (status === 400) return "Dados inválidos. Confira o que foi preenchido.";
  if (status === 401) return "Sua sessão expirou. Entre novamente.";
  if (status === 403) return "Você não tem permissão para esta ação.";
  if (status === 404) return "Não encontrado.";
  if (status === 409) return "Este item já existe.";
  if (status >= 500) return "Erro no servidor. Tente novamente em instantes.";
  return "Não foi possível concluir a ação.";
}

async function apiRequest(caminho, { method = "GET", body, autenticado = false } = {}) {
  const headers = {};
  if (body !== undefined) headers["Content-Type"] = "application/json";

  if (autenticado) {
    const token = await obterTokenValido();
    if (!token) throw new ApiError(401, "Sua sessão expirou. Entre novamente.");
    headers["Authorization"] = `Bearer ${token}`;
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
    if (resposta.status === 401 && autenticado) limparSessao();

    let mensagem = mensagemPadrao(resposta.status);
    if (typeof dados === "string" && dados.trim() && dados.length < 300) mensagem = dados;
    else if (dados && dados.mensagem) mensagem = dados.mensagem;

    throw new ApiError(resposta.status, mensagem);
  }
  return dados;
}

// usuário logado
function usuarioEmCache() {
  return estaLogado() ? lerArmazenado(CHAVE_USUARIO) : null;
}

async function carregarUsuarioLogado() {
  if (!estaLogado()) return null;
  try {
    const usuario = await SavePointApi.meusDados();
    gravarArmazenado(CHAVE_USUARIO, usuario);
    return usuario;
  } catch (erro) {
    if (erro.status === 401) return null;
    return usuarioEmCache();
  }
}

function atualizarUsuarioEmCache(usuario) {
  gravarArmazenado(CHAVE_USUARIO, usuario);
}

// rotas da API usadas pelas telas
const SavePointApi = {
  // autenticação
  login: (login, senha) =>
    apiRequest("/auth/login", { method: "POST", body: { email: login, senha } }),

  cadastrar: (nome, email, senha) =>
    apiRequest("/auth/register", { method: "POST", body: { nome, email, senha } }),

  // { disponivel: boolean, sugestoes: string[] }
  nomeDisponivel: (nome) =>
    apiRequest(`/auth/nome-disponivel?nome=${encodeURIComponent(nome)}`),

  // catálogo
  listarJogos: () => apiRequest("/jogos"),
  buscarJogo: (id) => apiRequest(`/jogos/${encodeURIComponent(id)}`),
  ofertasDoJogo: (id) => apiRequest(`/ofertas/${encodeURIComponent(id)}/comparacao`),
  historicoOferta: (ofertaId) => apiRequest(`/ofertas/${encodeURIComponent(ofertaId)}/historico`),

  // avaliações
  listarAvaliacoes: (jogoId) => apiRequest(`/jogos/${encodeURIComponent(jogoId)}/avaliacoes`),
    avaliar: (jogoId, { nota, textoAvaliacao, horasJogadas }) => {
      const usuario = usuarioEmCache();
      return apiRequest(`/jogos/${encodeURIComponent(jogoId)}/avaliacoes`, {
        method: "POST",
        body: {
          nota: Number(nota),
          textoAvaliacao,
          horasJogadas: Number(horasJogadas) || 0,
          curtidas: 0,
          usuario: { id: usuario?.id }
        },
        autenticado: true,
      });
    },

  // usuário
  meusDados: () => apiRequest("/usuarios/me", { autenticado: true }),
  atualizarUsuario: (id, dados) =>
    apiRequest(`/usuarios/${id}`, { method: "PUT", body: dados, autenticado: true }),

  // lista de desejos
  // POST na lista devolve a lista existente (ou cria uma vazia)
  obterListaDesejos: (usuarioId) =>
    apiRequest(`/usuarios/${usuarioId}/lista-desejos/`, { method: "POST", autenticado: true }),

  adicionarNaLista: (usuarioId, jogoId, precoAlerta, notificarOferta) =>
    apiRequest(`/usuarios/${usuarioId}/lista-desejos/itens`, {
      method: "POST",
      body: { jogo: { id: jogoId }, precoAlerta, notificarOferta },
      autenticado: true,
    }),

  atualizarItemLista: (usuarioId, itemId, precoAlerta, notificarOferta) =>
    apiRequest(`/usuarios/${usuarioId}/lista-desejos/itens/${itemId}`, {
      method: "PUT",
      body: { precoAlerta, notificarOferta },
      autenticado: true,
    }),

  removerItemLista: (usuarioId, itemId) =>
    apiRequest(`/usuarios/${usuarioId}/lista-desejos/itens/${itemId}`, {
      method: "DELETE",
      autenticado: true,
    }),
};
