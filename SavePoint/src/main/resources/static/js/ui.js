// utilitários
// impede que texto vindo do banco vire HTML (proteção contra XSS)
function escaparHtml(valor) {
  const mapa = { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" };
  return String(valor ?? "").replace(/[&<>"']/g, (c) => mapa[c]);
}

const formatadorPreco = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
function formatarPreco(valor) {
  return formatadorPreco.format(Number(valor) || 0);
}

function formatarData(texto) {
  if (!texto) return "";
  const [ano, mes, dia] = String(texto).slice(0, 10).split("-").map(Number);
  if (!ano || !mes || !dia) return "";
  const meses = ["jan.", "fev.", "mar.", "abr.", "mai.", "jun.", "jul.", "ago.", "set.", "out.", "nov.", "dez."];
  return `${dia} ${meses[mes - 1]} ${ano}`;
}

function listaGeneros(genero) {
  return String(genero || "")
    .split(",")
    .map((g) => g.trim())
    .filter(Boolean);
}

// só aceita links http/https
function urlExternaSegura(url) {
  return /^https?:\/\//i.test(String(url || "").trim()) ? String(url).trim() : null;
}

function parametroDaUrl(nome) {
  return new URLSearchParams(window.location.search).get(nome);
}

const ICONES = {
  lupa: '<svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" stroke-width="2"/><path d="m20 20-3.6-3.6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>',
  coracao: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 20.5s-7.5-4.6-9.3-9.2C1.5 8.2 3.4 4.5 7 4.5c2 0 3.4 1.1 5 3 1.6-1.9 3-3 5-3 3.6 0 5.5 3.7 4.3 6.8-1.8 4.6-9.3 9.2-9.3 9.2Z" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/></svg>',
  coracaoCheio: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 20.5s-7.5-4.6-9.3-9.2C1.5 8.2 3.4 4.5 7 4.5c2 0 3.4 1.1 5 3 1.6-1.9 3-3 5-3 3.6 0 5.5 3.7 4.3 6.8-1.8 4.6-9.3 9.2-9.3 9.2Z" fill="currentColor"/></svg>',
  imagem: '<svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="4" width="18" height="16" rx="2" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="9" cy="9.5" r="1.8" fill="currentColor"/><path d="m4 18 5.5-5.5 4 4 2.5-2.5L20 18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/></svg>',
  seta: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14M13 6l6 6-6 6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  externo: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M14 4h6v6M20 4l-9 9M18 14v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  menu: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 7h16M4 12h16M4 17h16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>',
  fechar: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 6 12 12M18 6 6 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>',
  sino: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 16V11a6 6 0 1 1 12 0v5l1.5 2h-15L6 16Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M10 20.5a2.2 2.2 0 0 0 4 0" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>',
};

// lista de endereços para tentar, na ordem: config.js (até 3) e, se sobrar vaga, o imagemCapa do banco
function fontesDaImagem(jogo) {
  const doConfig = (typeof IMAGENS_JOGOS !== "undefined" && IMAGENS_JOGOS[jogo.id]) || [];
  const candidatas = [...doConfig, jogo.imagemCapa];
  const validas = candidatas
    .map((url) => String(url || "").trim())
    .filter((url) => /^https?:\/\//i.test(url) || /^\/?assets\//.test(url));
  return [...new Set(validas)].slice(0, 3);
}

function htmlSemImagem() {
  return `<div class="sem-imagem" role="img" aria-label="Aguardando imagem">${ICONES.imagem}<span>Aguardando imagem</span></div>`;
}

// monta a área da capa
function htmlCapa(jogo, classeExtra = "") {
  const fontes = fontesDaImagem(jogo);
  const alt = `Capa de ${jogo.titulo || "jogo"}`;
  const conteudo = fontes.length
    ? `<img src="${escaparHtml(fontes[0])}" alt="${escaparHtml(alt)}" loading="lazy" decoding="async"
         data-fontes="${escaparHtml(JSON.stringify(fontes))}" data-indice="0">`
    : htmlSemImagem();
  return `<div class="capa ${classeExtra}">${conteudo}</div>`;
}

document.addEventListener(
  "error",
  (evento) => {
    const img = evento.target;
    if (!(img instanceof HTMLImageElement)) return;

    if (img.dataset.fontes) {
      const fontes = JSON.parse(img.dataset.fontes);
      const proximo = Number(img.dataset.indice || 0) + 1;
      if (proximo < fontes.length) {
        img.dataset.indice = String(proximo);
        img.src = fontes[proximo];
      } else {
        img.outerHTML = htmlSemImagem();
      }
      return;
    }

    if (img.dataset.avatar && !img.src.endsWith(AVATAR_PADRAO)) {
      img.src = AVATAR_PADRAO;
    }

    if (img.dataset.logoLoja) {
      img.remove();
    }
  },
  true
);


const AVATAR_PADRAO = "/assets/avatar-padrao.svg";

function urlAvatar(usuario) {
  const foto = usuario && usuario.fotoPerfil;
  return foto && /^\/assets\/avatares\/[A-Za-z0-9_-]+\.(png|jpe?g|webp|svg)$/.test(foto) ? foto : AVATAR_PADRAO;
}

function htmlAvatar(usuario, classe = "avatar") {
  const nome = usuario && usuario.nome ? usuario.nome : "Usuário";
  return `<img class="${classe}" src="${escaparHtml(urlAvatar(usuario))}" alt="Foto de ${escaparHtml(nome)}" data-avatar="1">`;
}

// login obrigatório para a lista de desejos

function irParaLogin(motivo = "", voltar = null) {
  const destino = voltar || window.location.pathname.replace(/^\//, "") + window.location.search;
  const params = new URLSearchParams();
  if (motivo) params.set("motivo", motivo);
  if (destino) params.set("voltar", destino);
  window.location.href = `login.html?${params.toString()}`;
}

// qualquer link/botão com data-protegido="wishlist" exige login
document.addEventListener("click", (evento) => {
  const alvo = evento.target.closest('[data-protegido="wishlist"]');
  if (!alvo || estaLogado()) return;
  evento.preventDefault();
  irParaLogin("wishlist", "wishlist.html");
});

function mostrarAviso(mensagem, tipo = "") {
  let area = document.querySelector("#area-avisos");
  if (!area) {
    area = document.createElement("div");
    area.id = "area-avisos";
    area.className = "area-avisos";
    area.setAttribute("role", "status");
    area.setAttribute("aria-live", "polite");
    document.body.append(area);
  }
  const aviso = document.createElement("div");
  aviso.className = `aviso ${tipo}`;
  aviso.textContent = mensagem;
  area.append(aviso);
  setTimeout(() => aviso.classList.add("saindo"), 3200);
  setTimeout(() => aviso.remove(), 3600);
}

let promessaUsuario = null;
function obterUsuario() {
  if (!promessaUsuario) promessaUsuario = carregarUsuarioLogado();
  return promessaUsuario;
}

function montarCabecalho() {
  const destino = document.querySelector("#cabecalho");
  if (!destino) return;

  const pagina = document.body.dataset.pagina || "";
  const ativo = (nome) => (pagina === nome ? ' class="active" aria-current="page"' : "");
  const busca = pagina === "jogos" ? escaparHtml(parametroDaUrl("q") || "") : "";

  destino.outerHTML = `
    <header class="site-header">
      <button class="menu-toggle icon-button" type="button" aria-expanded="false" aria-controls="main-nav" aria-label="Abrir menu">${ICONES.menu}</button>
      <nav class="main-nav" id="main-nav" aria-label="Navegação principal">
        <a href="index.html"${ativo("inicio")}>Início</a>
        <a href="jogos.html"${ativo("jogos")}>Jogos</a>
        <a href="wishlist.html" data-protegido="wishlist"${ativo("wishlist")}>Wishlist</a>
        <a href="index.html#sobre">Sobre</a>
      </nav>
      <form class="header-search" action="jogos.html" method="get" role="search">
        <label class="sr-only" for="busca-topo">Procurar jogos</label>
        <span class="header-search-icon">${ICONES.lupa}</span>
        <input id="busca-topo" name="q" type="search" placeholder="Procure..." autocomplete="off" value="${busca}">
      </form>
      <div class="header-actions" id="acoes-usuario"></div>
    </header>`;

  // menu no celular
  const botaoMenu = document.querySelector(".menu-toggle");
  const nav = document.querySelector("#main-nav");
  botaoMenu.addEventListener("click", () => {
    const aberto = nav.classList.toggle("open");
    botaoMenu.setAttribute("aria-expanded", String(aberto));
  });

  renderizarAcoesUsuario(usuarioEmCache());
  if (estaLogado()) obterUsuario().then((usuario) => renderizarAcoesUsuario(usuario));
}

function renderizarAcoesUsuario(usuario) {
  const area = document.querySelector("#acoes-usuario");
  if (!area) return;

  if (!estaLogado()) {
    area.innerHTML = `<a class="btn-entrar" href="login.html">Entrar</a>`;
    return;
  }

  const nome = usuario && usuario.nome ? usuario.nome : "Minha conta";
  area.innerHTML = `
    <div class="menu-usuario">
      <button class="botao-avatar" type="button" aria-haspopup="true" aria-expanded="false" aria-label="Menu da conta">
        ${htmlAvatar(usuario, "avatar avatar-topo")}
      </button>
      <div class="menu-usuario-lista" hidden>
        <p class="menu-usuario-nome">${escaparHtml(nome)}</p>
        <a href="perfil.html">Meu perfil</a>
        <a href="wishlist.html">Lista de desejos</a>
        <button type="button" data-acao="sair">Sair</button>
      </div>
    </div>`;

  const botao = area.querySelector(".botao-avatar");
  const lista = area.querySelector(".menu-usuario-lista");
  const fechar = () => {
    lista.hidden = true;
    botao.setAttribute("aria-expanded", "false");
  };
  botao.addEventListener("click", (evento) => {
    evento.stopPropagation();
    lista.hidden = !lista.hidden;
    botao.setAttribute("aria-expanded", String(!lista.hidden));
  });
  document.addEventListener("click", (evento) => {
    if (!evento.target.closest(".menu-usuario")) fechar();
  });
  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape") fechar();
  });
  area.querySelector('[data-acao="sair"]').addEventListener("click", () => {
    limparSessao();
    window.location.href = "index.html";
  });
}

function montarRodape() {
  const destino = document.querySelector("#rodape");
  if (!destino) return;
  destino.outerHTML = `
    <footer class="site-footer">
      <div class="footer-main">
        <a class="footer-brand" href="index.html"><img src="/assets/logo-recortada.png" alt="SavePoint" class="brand-logo"></a>
        <nav aria-label="Navegação do rodapé">
          <a href="index.html">Início</a>
          <a href="jogos.html">Jogos</a>
          <a href="wishlist.html" data-protegido="wishlist">Wishlist</a>
          <a href="index.html#sobre">Sobre</a>
        </nav>
      </div>
      <div class="footer-bottom">
        <span>© 2026 SavePoint. Não vendemos jogos: mostramos onde comprar pelo menor preço.</span>
        <span>Preços e descontos informados pelas lojas parceiras e sujeitos a alteração.</span>
      </div>
    </footer>`;
}

// catálogo
// busca todos os jogos e, em paralelo, as ofertas de cada um (a mais barata vem primeiro)
async function carregarCatalogo() {
  const jogos = await SavePointApi.listarJogos();
  const ofertas = await Promise.all(
    jogos.map((jogo) => SavePointApi.ofertasDoJogo(jogo.id).catch(() => []))
  );
  return jogos.map((jogo, i) => {
    const lista = Array.isArray(ofertas[i]) ? ofertas[i] : [];
    return { jogo, ofertas: lista, melhorOferta: lista[0] || null };
  });
}

function descontoDe(entrada) {
  return entrada.melhorOferta ? Math.round(entrada.melhorOferta.percentualDesconto || 0) : 0;
}

function htmlPreco(oferta) {
  if (!oferta) return `<div class="price-row"><span class="sem-preco">Sem oferta no momento</span></div>`;
  const desconto = Math.round(oferta.percentualDesconto || 0);
  return `
    <div class="price-row">
      ${desconto > 0 ? `<span class="discount">-${desconto}%</span>` : ""}
      <div class="price-values">
        ${desconto > 0 ? `<del>${formatarPreco(oferta.precoOriginal)}</del>` : ""}
        <strong>${formatarPreco(oferta.precoAtual)}</strong>
      </div>
    </div>`;
}

// =botão de lista de desejos
function htmlBotaoWishlist(jogo, classe = "wish-btn") {
  return `<button class="${classe}" type="button" data-acao="wishlist" data-jogo-id="${jogo.id}"
            data-titulo="${escaparHtml(jogo.titulo)}" aria-pressed="false"
            aria-label="Adicionar ${escaparHtml(jogo.titulo)} à lista de desejos">
            <span class="icone-vazio">${ICONES.coracao}</span><span class="icone-cheio">${ICONES.coracaoCheio}</span>
          </button>`;
}

// cards
function htmlCardJogo(entrada) {
  const { jogo, melhorOferta } = entrada;
  const generos = listaGeneros(jogo.genero).slice(0, 3).map(escaparHtml).join(" <span>•</span> ");
  const loja = melhorOferta && melhorOferta.loja ? escaparHtml(melhorOferta.loja.nome) : "";
  return `
    <article class="game-card">
      <a class="game-card-link" href="jogo.html?id=${jogo.id}">
        ${htmlCapa(jogo, "capa-card")}
        <div class="game-card-body">
          <h3>${escaparHtml(jogo.titulo)}</h3>
          <p class="game-tags">${generos || "Gênero não informado"}</p>
          ${htmlPreco(melhorOferta)}
          ${loja ? `<p class="store-name">em <strong>${loja}</strong></p>` : ""}
        </div>
      </a>
      ${htmlBotaoWishlist(jogo)}
    </article>`;
}

function htmlLinhaJogo(entrada) {
  const { jogo, melhorOferta } = entrada;
  const generos = listaGeneros(jogo.genero).map(escaparHtml).join(", ");
  return `
    <article class="game-row">
      <a class="game-row-link" href="jogo.html?id=${jogo.id}">
        ${htmlCapa(jogo, "capa-linha")}
        <div class="game-row-info">
          <h3>${escaparHtml(jogo.titulo)}</h3>
          <p>${generos || "Gênero não informado"}</p>
          ${jogo.dataLancamento ? `<p class="game-row-data">Lançamento: ${formatarData(jogo.dataLancamento)}</p>` : ""}
        </div>
        <div class="game-row-preco">${htmlPreco(melhorOferta)}</div>
      </a>
      ${htmlBotaoWishlist(jogo, "wish-btn wish-btn-linha")}
    </article>`;
}

function htmlSkeletonCards(quantidade = 4) {
  return Array.from({ length: quantidade }, () => `<div class="game-card skeleton skeleton-card" aria-hidden="true"></div>`).join("");
}

function htmlSkeletonLinhas(quantidade = 5) {
  return Array.from({ length: quantidade }, () => `<div class="skeleton skeleton-row" aria-hidden="true"></div>`).join("");
}

const ListaDesejos = {
  usuarioId: null,
  itens: new Map(), // jogoId -> item { id, precoAlerta, notificarOferta, jogo }
  carregada: false,
  promessa: null,

  // busca a lista uma única vez por página; quem chamar de novo recebe a mesma
  carregar() {
    if (!this.promessa) this.promessa = this.buscar();
    return this.promessa;
  },

  async buscar() {
    if (!estaLogado()) return;
    const usuario = await obterUsuario();
    if (!usuario) return;
    this.usuarioId = usuario.id;
    const lista = await SavePointApi.obterListaDesejos(usuario.id);
    const itens = (lista && Array.isArray(lista.jogos)) ? lista.jogos : [];
    this.itens = new Map(itens.filter((i) => i.jogo).map((i) => [i.jogo.id, i]));
    this.carregada = true;
    this.avisarMudanca();
  },

  tem(jogoId) {
    return this.itens.has(Number(jogoId));
  },

  item(jogoId) {
    return this.itens.get(Number(jogoId)) || null;
  },

  async adicionar(jogoId, precoAlerta, notificarOferta) {
    const item = await SavePointApi.adicionarNaLista(this.usuarioId, Number(jogoId), precoAlerta, notificarOferta);
    this.itens.set(Number(jogoId), item);
    this.avisarMudanca();
    return item;
  },

  async atualizar(jogoId, precoAlerta, notificarOferta) {
    const atual = this.item(jogoId);
    if (!atual) return null;
    const item = await SavePointApi.atualizarItemLista(this.usuarioId, atual.id, precoAlerta, notificarOferta);
    this.itens.set(Number(jogoId), { ...atual, ...item, jogo: atual.jogo });
    this.avisarMudanca();
    return item;
  },

  async remover(jogoId) {
    const atual = this.item(jogoId);
    if (!atual) return;
    await SavePointApi.removerItemLista(this.usuarioId, atual.id);
    this.itens.delete(Number(jogoId));
    this.avisarMudanca();
  },

  avisarMudanca() {
    document.dispatchEvent(new CustomEvent("wishlist:mudou"));
  },
};

function atualizarBotoesWishlist() {
  document.querySelectorAll('[data-acao="wishlist"]').forEach((botao) => {
    const naLista = ListaDesejos.tem(botao.dataset.jogoId);
    const titulo = botao.dataset.titulo || "jogo";
    botao.classList.toggle("na-lista", naLista);
    botao.setAttribute("aria-pressed", String(naLista));
    botao.setAttribute("aria-label", naLista ? `Remover ${titulo} da lista de desejos` : `Adicionar ${titulo} à lista de desejos`);
    const texto = botao.querySelector(".wish-texto");
    if (texto) texto.textContent = naLista ? "Na sua lista de desejos" : "Adicionar à lista de desejos";
  });
}
document.addEventListener("wishlist:mudou", atualizarBotoesWishlist);

document.addEventListener("click", async (evento) => {
  const botao = evento.target.closest('[data-acao="wishlist"]');
  if (!botao) return;
  evento.preventDefault();

  if (!estaLogado()) {
    irParaLogin("wishlist");
    return;
  }

  const jogoId = Number(botao.dataset.jogoId);
  if (ListaDesejos.tem(jogoId)) {
    botao.disabled = true;
    try {
      await ListaDesejos.remover(jogoId);
      mostrarAviso("Removido da sua lista de desejos.");
    } catch (erro) {
      mostrarAviso(erro.message, "erro");
    } finally {
      botao.disabled = false;
    }
  } else {
    abrirJanelaAlerta({ jogoId, titulo: botao.dataset.titulo || "este jogo" });
  }
});

// janela "adicionar à lista / editar alerta de preço"
function abrirJanelaAlerta({ jogoId, titulo, precoAtual = null }) {
  const existente = ListaDesejos.item(jogoId);
  const editando = Boolean(existente);
  const valorAlerta = existente && existente.precoAlerta > 0 ? existente.precoAlerta : "";
  const notificar = existente ? existente.notificarOferta : true;

  document.querySelector("#janela-alerta")?.remove();
  const janela = document.createElement("dialog");
  janela.id = "janela-alerta";
  janela.className = "janela";
  janela.innerHTML = `
    <form method="dialog" class="janela-conteudo" novalidate>
      <button class="janela-fechar icon-button" type="button" data-fechar aria-label="Fechar">${ICONES.fechar}</button>
      <p class="section-kicker">${editando ? "EDITAR ALERTA" : "LISTA DE DESEJOS"}</p>
      <h2>${escaparHtml(titulo)}</h2>
      <label class="campo-check">
        <input type="checkbox" name="notificar" ${notificar ? "checked" : ""}>
        <span>Quero ser avisado por e-mail</span>
      </label>
      <div class="campo">
        <label for="preco-alerta">Avise quando o menor preço for até <small>(opcional)</small></label>
        <div class="campo-moeda"><span>R$</span><input id="preco-alerta" name="preco" type="number" min="0" step="0.01" inputmode="decimal" placeholder="Qualquer promoção" value="${valorAlerta}"></div>
        <small>${precoAtual ? `Menor preço hoje: ${formatarPreco(precoAtual)}. ` : ""}Sem valor, avisamos em qualquer promoção.</small>
      </div>
      <p class="janela-erro" hidden></p>
      <div class="janela-acoes">
        <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
        <button class="botao-primario" type="submit">${editando ? "Salvar alerta" : "Adicionar à lista"}</button>
      </div>
    </form>`;
  document.body.append(janela);

  const form = janela.querySelector("form");
  const erro = janela.querySelector(".janela-erro");
  const campoPreco = janela.querySelector("#preco-alerta");
  const campoNotificar = janela.querySelector('[name="notificar"]');
  campoPreco.disabled = !campoNotificar.checked;
  campoNotificar.addEventListener("change", () => (campoPreco.disabled = !campoNotificar.checked));

  janela.querySelectorAll("[data-fechar]").forEach((b) => b.addEventListener("click", () => janela.close()));

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const preco = campoPreco.value === "" ? 0 : Number(campoPreco.value);
    if (!Number.isFinite(preco) || preco < 0) {
      erro.textContent = "Informe um valor válido ou deixe em branco.";
      erro.hidden = false;
      return;
    }

    const botaoSalvar = form.querySelector('[type="submit"]');
    botaoSalvar.disabled = true;
    try {
      if (editando) {
        await ListaDesejos.atualizar(jogoId, preco, campoNotificar.checked);
        mostrarAviso("Alerta atualizado.", "sucesso");
      } else {
        await ListaDesejos.adicionar(jogoId, preco, campoNotificar.checked);
        mostrarAviso("Adicionado à sua lista de desejos.", "sucesso");
      }
      janela.close();
    } catch (falha) {
      erro.textContent = falha.message;
      erro.hidden = false;
      botaoSalvar.disabled = false;
    }
  });

  janela.addEventListener("close", () => janela.remove());
  janela.showModal();
}

// resumo avaliações
function resumoAvaliacoes(avaliacoes) {
  const total = avaliacoes.length;
  if (!total) return { total: 0, media: 0, percentual: 0, rotulo: "Sem avaliações", classe: "neutro" };
  const media = avaliacoes.reduce((soma, a) => soma + (Number(a.nota) || 0), 0) / total;
  const recomendam = avaliacoes.filter((a) => Number(a.nota) >= 4).length;
  const percentual = Math.round((recomendam / total) * 100);
  let rotulo = "Ligeiramente negativas";
  let classe = "negativo";
  if (percentual >= 80) { rotulo = "Muito positivas"; classe = "positivo"; }
  else if (percentual >= 70) { rotulo = "Positivas"; classe = "positivo"; }
  else if (percentual >= 40) { rotulo = "Neutras"; classe = "misto"; }
  return { total, media, percentual, rotulo, classe };
}

function htmlEstrelas(nota) {
  const n = Math.max(0, Math.min(5, Math.round(Number(nota) || 0)));
  return `<span class="estrelas" aria-label="Nota ${n} de 5">${"★".repeat(n)}<span class="estrelas-vazias">${"★".repeat(5 - n)}</span></span>`;
}

// inicialização comum - todas as páginas

document.addEventListener("DOMContentLoaded", () => {
  montarCabecalho();
  montarRodape();
  if (estaLogado()) {
    ListaDesejos.carregar().catch(() => {
    });
  }
});
