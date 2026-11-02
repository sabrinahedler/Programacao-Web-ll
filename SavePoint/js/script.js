const searchForm = document.querySelector("#search-form");
const searchInput = document.querySelector("#game-search");
const filterButtons = [...document.querySelectorAll("[data-filter]")];
const focusSearchButton = document.querySelector("#focus-search");
const menuToggle = document.querySelector(".menu-toggle");
const mainNav = document.querySelector("#main-nav");
const aiForm = document.querySelector("#ai-form");
const aiQuestion = document.querySelector("#ai-question");
const aiFeedback = document.querySelector("#ai-feedback");
const wishlistButton = document.querySelector("#wishlist-button");

// passam a receber dados do back-end
const dealGrid = document.querySelector("#deal-grid");
const dealsNotice = document.querySelector("#deals-notice");
const discountList = document.querySelector("#discount-list");
const navAuth = document.querySelector("#nav-auth");

const formatadorPreco = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

// estado da página
let catalogo = [];

// impede que um texto vindo do banco seja interpretado como HTML (proteção contra XSS)
function escaparHtml(valor) {
  const mapa = { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" };
  return String(valor ?? "").replace(/[&<>"']/g, (caractere) => mapa[caractere]);
}

function formatarGeneros(genero) {
  if (!genero) {
    return "Gênero não informado";
  }

  return genero
      .split(",")
      .map((item) => escaparHtml(item.trim()))
      .filter(Boolean)
      .join(" <span>•</span> ");
}

function obterIniciais(titulo) {
  return (titulo || "?")
      .split(/\s+/)
      .slice(0, 2)
      .map((palavra) => palavra[0])
      .join("")
      .toUpperCase();
}

function temDesconto(oferta) {
  return Boolean(oferta) && oferta.percentualDesconto > 0;
}

function criarArte(jogo) {
  const titulo = escaparHtml(jogo.titulo);
  const urlValida = /^https?:\/\//i.test(jogo.imagemCapa || "");

  if (urlValida) {
    return `<div class="game-art"><img src="${escaparHtml(jogo.imagemCapa)}" alt="Capa de ${titulo}" loading="lazy"></div>`;
  }

  return `<div class="game-art art-generica" role="img" aria-label="Capa de ${titulo}"><span>${titulo.toUpperCase()}</span></div>`;
}

function criarPreco(oferta) {
  if (!estaLogado()) {
    return `<p class="store-name"><a class="text-link" href="login.html">Entre para ver os preços →</a></p>`;
  }

  if (!oferta) {
    return `<p class="store-name">Nenhuma oferta cadastrada para este jogo.</p>`;
  }

  const desconto = Math.round(oferta.percentualDesconto);
  const loja = escaparHtml(oferta.loja?.nome ?? "Loja");

  return `
    <div class="price-row">
      ${desconto > 0 ? `<span class="discount">-${desconto}%</span>` : ""}
      <strong>${formatadorPreco.format(oferta.precoAtual)}</strong>
      ${desconto > 0 ? `<del>${formatadorPreco.format(oferta.precoOriginal)}</del>` : ""}
    </div>
    <p class="store-name">◉ ${loja} <span class="deal-status">Menor preço</span></p>`;
}

function criarCardJogo({ jogo, melhorOferta }) {
  const titulo = escaparHtml(jogo.titulo);

  return `
    <article class="game-card" data-title="${titulo}" data-sale="${temDesconto(melhorOferta)}">
      ${criarArte(jogo)}
      <div class="game-card-body">
        <div class="game-title-row"><h3>${titulo}</h3><span class="store-label">PC</span></div>
        <p class="game-tags">${formatarGeneros(jogo.genero)}</p>
        ${criarPreco(melhorOferta)}
      </div>
    </article>`;
}

function criarLinhaDesconto({ jogo, melhorOferta }) {
  return `
    <article class="discount-row">
      <div class="mini-cover mini-generica">${escaparHtml(obterIniciais(jogo.titulo))}</div>
      <div class="row-game"><strong>${escaparHtml(jogo.titulo)}</strong><small>${escaparHtml(jogo.genero ?? "")}</small></div>
      <span class="discount">-${Math.round(melhorOferta.percentualDesconto)}%</span>
      <div class="row-price"><strong>${formatadorPreco.format(melhorOferta.precoAtual)}</strong><del>${formatadorPreco.format(melhorOferta.precoOriginal)}</del></div>
    </article>`;
}

function renderizarJogos() {
  dealGrid.removeAttribute("aria-busy");

  if (catalogo.length === 0) {
    dealGrid.innerHTML = "";
    dealsNotice.textContent = "Nenhum jogo cadastrado ainda.";
    return;
  }

  // visitante não vê preços, então "Em promoção" esconderia todos os jogos
  if (!estaLogado()) {
    selecionarFiltro("all");
  }

  dealGrid.innerHTML = catalogo.map(criarCardJogo).join("");
  dealsNotice.textContent = estaLogado()
      ? "Menor preço encontrado entre as lojas cadastradas no SavePoint."
      : "Entre na sua conta para ver preços e descontos.";

  aplicarFiltroAtual();
}

function renderizarDescontos() {
  discountList.removeAttribute("aria-busy");

  if (!estaLogado()) {
    discountList.innerHTML = `<p class="demo-notice"><a class="text-link" href="login.html">Entre na sua conta</a> para ver os maiores descontos.</p>`;
    return;
  }

  const maioresDescontos = catalogo
      .filter((item) => temDesconto(item.melhorOferta))
      .sort((a, b) => b.melhorOferta.percentualDesconto - a.melhorOferta.percentualDesconto)
      .slice(0, 4);

  discountList.innerHTML = maioresDescontos.length
      ? maioresDescontos.map(criarLinhaDesconto).join("")
      : `<p class="demo-notice">Nenhum jogo em promoção no momento.</p>`;
}

function mostrarErroCarregamento(mensagem) {
  dealGrid.removeAttribute("aria-busy");
  discountList.removeAttribute("aria-busy");
  dealGrid.innerHTML = "";
  dealsNotice.textContent = mensagem;
  discountList.innerHTML = `<p class="demo-notice">${escaparHtml(mensagem)}</p>`;
}

async function buscarMelhorOferta(jogoId) {
  try {
    const ofertas = await SavePointApi.compararOfertas(jogoId);
    // O back-end já devolve ordenado do menor para o maior preço.
    return ofertas[0] ?? null;
  } catch {
    return null;
  }
}

async function carregarCatalogo() {
  try {
    const jogos = await SavePointApi.listarJogos();

    const melhoresOfertas = estaLogado()
        ? await Promise.all(jogos.map((jogo) => buscarMelhorOferta(jogo.id)))
        : jogos.map(() => null);

    catalogo = jogos
        .map((jogo, indice) => ({ jogo, melhorOferta: melhoresOfertas[indice] }))
        .sort((a, b) => (b.melhorOferta?.percentualDesconto ?? -1) - (a.melhorOferta?.percentualDesconto ?? -1));

    atualizarMenu();
    renderizarJogos();
    renderizarDescontos();
  } catch (erro) {
    mostrarErroCarregamento(erro.message);
  }
}

function atualizarMenu() {
  if (estaLogado()) {
    navAuth.textContent = "Sair";
    navAuth.href = "#";
  } else {
    navAuth.textContent = "Entrar";
    navAuth.href = "login.html";
  }
}

function filterGames(query = "", filter = "all") {
  const gameCards = [...dealGrid.querySelectorAll(".game-card")];
  const normalizedQuery = query.trim().toLocaleLowerCase("pt-BR");
  let visibleCount = 0;

  gameCards.forEach((card) => {
    const title = (card.dataset.title || "").toLocaleLowerCase("pt-BR");
    const matchesQuery = title.includes(normalizedQuery);
    const matchesFilter = filter !== "sale" || card.dataset.sale === "true";
    const isVisible = matchesQuery && matchesFilter;
    card.hidden = !isVisible;
    if (isVisible) visibleCount++;
  });

  let message = document.querySelector("#search-feedback");
  if (!message) {
    message = document.createElement("p");
    message.id = "search-feedback";
    message.className = "demo-notice";
    dealGrid.after(message);
  }

  if (gameCards.length > 0 && visibleCount === 0) {
    message.textContent = "Nenhum jogo encontrado com esses filtros.";
  } else if (normalizedQuery) {
    message.textContent = `${visibleCount} jogo(s) encontrado(s).`;
  } else {
    message.textContent = "";
  }
}

function filtroSelecionado() {
  return document.querySelector(".filter-chip.selected")?.dataset.filter || "all";
}

function selecionarFiltro(filter) {
  filterButtons.forEach((item) => item.classList.toggle("selected", item.dataset.filter === filter));
}

function aplicarFiltroAtual() {
  filterGames(searchInput.value, filtroSelecionado());
}

searchForm.addEventListener("submit", (event) => {
  event.preventDefault();
  aplicarFiltroAtual();
  document.querySelector("#ofertas").scrollIntoView({ behavior: "smooth" });
});

filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    selecionarFiltro(button.dataset.filter);
    aplicarFiltroAtual();
  });
});

navAuth.addEventListener("click", (event) => {
  if (!estaLogado()) {
    return; // segue o link normal para login.html
  }

  event.preventDefault();
  limparSessao();
  window.location.reload();
});

focusSearchButton.addEventListener("click", () => {
  searchInput.focus();
  document.querySelector("#inicio").scrollIntoView({ behavior: "smooth" });
});

menuToggle.addEventListener("click", () => {
  const isOpen = mainNav.classList.toggle("open");
  menuToggle.setAttribute("aria-expanded", String(isOpen));
});

aiForm.addEventListener("submit", (event) => {
  event.preventDefault();
  const question = aiQuestion.value.trim();
  aiFeedback.textContent = question
      ? "A interface está pronta para receber a integração com o serviço de IA. Ainda não enviamos sua pergunta a um servidor."
      : "Digite o nome de um jogo ou sua dúvida para testar este campo.";
});

wishlistButton.addEventListener("click", () => {
  alert("A wishlist será implementada em uma próxima etapa. Por enquanto, este é apenas um protótipo visual.");
});


atualizarMenu();
carregarCatalogo();