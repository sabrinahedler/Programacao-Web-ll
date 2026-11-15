let catalogoHome = [];
let timerCarrossel = null;

document.addEventListener("DOMContentLoaded", () => {
  // esqueletos de carregamento enquanto a API responde
  document.querySelector("#grade-ofertas").innerHTML = htmlSkeletonCards(8);
  document.querySelector("#lista-descontos").innerHTML = htmlSkeletonLinhas(5);
  document.querySelector("#lista-explorar").innerHTML = htmlSkeletonLinhas(6);

  configurarAbasExplorar();
  configurarAssistente();
  carregarHome();
});

async function carregarHome() {
  try {
    catalogoHome = await carregarCatalogo();
  } catch (erro) {
    mostrarErroHome(erro.message);
    return;
  }

  if (catalogoHome.length === 0) {
    mostrarErroHome("Nenhum jogo cadastrado ainda.");
    return;
  }

  renderizarCarrossel();
  renderizarOfertas();
  renderizarDescontos();
  renderizarExplorar("lancamento");
  renderizarGeneros();
  atualizarBotoesWishlist();
}

function mostrarErroHome(mensagem) {
  const texto = escaparHtml(mensagem);
  document.querySelector("#carrossel").innerHTML = `<p class="estado-vazio">${texto}</p>`;
  document.querySelector("#grade-ofertas").innerHTML = "";
  document.querySelector("#aviso-ofertas").textContent = mensagem;
  document.querySelector("#lista-descontos").innerHTML = `<p class="demo-notice">${texto}</p>`;
  document.querySelector("#lista-explorar").innerHTML = `<p class="demo-notice">${texto}</p>`;
  ["#grade-ofertas", "#lista-descontos", "#lista-explorar"].forEach((s) => document.querySelector(s).removeAttribute("aria-busy"));
}

// ordenações
function porDesconto(lista) {
  return [...lista].sort((a, b) => descontoDe(b) - descontoDe(a));
}

function porLancamento(lista) {
  return [...lista].sort((a, b) => String(b.jogo.dataLancamento || "").localeCompare(String(a.jogo.dataLancamento || "")));
}

function porPreco(lista) {
  return lista
    .filter((e) => e.melhorOferta)
    .sort((a, b) => a.melhorOferta.precoAtual - b.melhorOferta.precoAtual);
}


// carrossel "em destaque agora"
function renderizarCarrossel() {
  const area = document.querySelector("#carrossel");
  const promocoes = porDesconto(catalogoHome).filter((e) => descontoDe(e) > 0);
  const itens = (promocoes.length ? promocoes : porLancamento(catalogoHome)).slice(0, 5);

  area.innerHTML = `
    <div class="carrossel-trilho">
      ${itens.map((entrada, i) => htmlSlide(entrada, i, itens.length)).join("")}
    </div>
    ${itens.length > 1 ? `
      <button class="carrossel-seta anterior" type="button" aria-label="Destaque anterior">‹</button>
      <button class="carrossel-seta proxima" type="button" aria-label="Próximo destaque">›</button>
      <div class="carrossel-pontos">
        ${itens.map((_, i) => `<button type="button" class="ponto${i === 0 ? " ativo" : ""}" data-slide="${i}" aria-label="Ir para o destaque ${i + 1}"></button>`).join("")}
      </div>` : ""}`;

  let atual = 0;
  const slides = [...area.querySelectorAll(".slide")];
  const pontos = [...area.querySelectorAll(".ponto")];

  const mostrar = (indice) => {
    atual = (indice + slides.length) % slides.length;
    slides.forEach((s, i) => {
      s.classList.toggle("ativo", i === atual);
      s.setAttribute("aria-hidden", String(i !== atual));
      s.querySelectorAll("a, button").forEach((el) => (el.tabIndex = i === atual ? 0 : -1));
    });
    pontos.forEach((p, i) => p.classList.toggle("ativo", i === atual));
  };

  area.querySelector(".anterior")?.addEventListener("click", () => mostrar(atual - 1));
  area.querySelector(".proxima")?.addEventListener("click", () => mostrar(atual + 1));
  pontos.forEach((p) => p.addEventListener("click", () => mostrar(Number(p.dataset.slide))));

  // troca sozinho a cada 7 segundos
  const reduzirMovimento = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  const iniciar = () => {
    if (reduzirMovimento || slides.length < 2) return;
    clearInterval(timerCarrossel);
    timerCarrossel = setInterval(() => mostrar(atual + 1), 7000);
  };
  const parar = () => clearInterval(timerCarrossel);
  area.addEventListener("mouseenter", parar);
  area.addEventListener("mouseleave", iniciar);
  area.addEventListener("focusin", parar);
  area.addEventListener("focusout", iniciar);

  mostrar(0);
  iniciar();
}

function htmlSlide(entrada, indice, total) {
  const { jogo, melhorOferta, ofertas } = entrada;
  const generos = listaGeneros(jogo.genero).slice(0, 4).map((g) => `<span class="tag">${escaparHtml(g)}</span>`).join("");
  return `
    <article class="slide${indice === 0 ? " ativo" : ""}" aria-roledescription="slide" aria-label="${indice + 1} de ${total}">
      <a class="slide-capa" href="jogo.html?id=${jogo.id}">${htmlCapa(jogo, "capa-destaque")}</a>
      <div class="slide-info">
        <h3><a href="jogo.html?id=${jogo.id}">${escaparHtml(jogo.titulo)}</a></h3>
        <p class="slide-descricao">${escaparHtml(jogo.descricao || "")}</p>
        <div class="tags">${generos}</div>
        <p class="slide-lojas">${ofertas.length ? `Disponível em ${ofertas.length} loja${ofertas.length > 1 ? "s" : ""}` : "Sem lojas cadastradas"}</p>
        <div class="slide-rodape">
          ${htmlPreco(melhorOferta)}
          ${htmlBotaoWishlist(jogo, "wish-btn wish-btn-texto")}
        </div>
      </div>
    </article>`;
}

// ofertas especiais
function renderizarOfertas() {
  const grade = document.querySelector("#grade-ofertas");
  const aviso = document.querySelector("#aviso-ofertas");
  const promocoes = porDesconto(catalogoHome).filter((e) => descontoDe(e) > 0);
  const lista = (promocoes.length ? promocoes : catalogoHome).slice(0, 8);

  grade.removeAttribute("aria-busy");
  grade.innerHTML = lista.map(htmlCardJogo).join("");
  aviso.textContent = promocoes.length
    ? "Menor preço encontrado entre as lojas cadastradas no SavePoint."
    : "Nenhum jogo em promoção no momento. Mostrando o catálogo.";
}

// maiores descontos do dia
function renderizarDescontos() {
  const area = document.querySelector("#lista-descontos");
  area.removeAttribute("aria-busy");
  const lista = porDesconto(catalogoHome).filter((e) => descontoDe(e) > 0).slice(0, 5);

  if (!lista.length) {
    area.innerHTML = `<p class="demo-notice">Nenhum jogo em promoção no momento.</p>`;
    return;
  }

  area.innerHTML = lista
    .map(({ jogo, melhorOferta }) => `
      <a class="discount-row" href="jogo.html?id=${jogo.id}">
        ${htmlCapa(jogo, "mini-cover")}
        <div class="row-game"><strong>${escaparHtml(jogo.titulo)}</strong><small>${escaparHtml(listaGeneros(jogo.genero).join(" · "))}</small></div>
        <span class="discount">-${Math.round(melhorOferta.percentualDesconto)}%</span>
        <div class="row-price"><strong>${formatarPreco(melhorOferta.precoAtual)}</strong><del>${formatarPreco(melhorOferta.precoOriginal)}</del></div>
      </a>`)
    .join("");
}

// explorar
function configurarAbasExplorar() {
  document.querySelectorAll("#explorar .aba").forEach((aba) => {
    aba.addEventListener("click", () => {
      document.querySelectorAll("#explorar .aba").forEach((a) => {
        a.classList.toggle("ativa", a === aba);
        a.setAttribute("aria-selected", String(a === aba));
      });
      renderizarExplorar(aba.dataset.lista);
    });
  });
}

function renderizarExplorar(tipo) {
  const area = document.querySelector("#lista-explorar");
  const link = document.querySelector("#link-explorar");
  if (!catalogoHome.length) return;

  let lista = porLancamento(catalogoHome);
  let linkVerTodos = "jogos.html?ordem=lancamento";
  if (tipo === "preco") {
    lista = porPreco(catalogoHome);
    linkVerTodos = "jogos.html?ordem=preco";
  }
  if (tipo === "desconto") {
    lista = porDesconto(catalogoHome).filter((e) => descontoDe(e) > 0);
    linkVerTodos = "jogos.html?promo=1&ordem=desconto";
  }

  area.removeAttribute("aria-busy");
  link.href = linkVerTodos;
  area.innerHTML = lista.length
    ? lista.slice(0, 6).map(htmlLinhaJogo).join("")
    : `<p class="estado-vazio">Nenhum jogo nesta lista ainda.</p>`;
  atualizarBotoesWishlist();
}

// generos
function renderizarGeneros() {
  const contagem = new Map();
  catalogoHome.forEach(({ jogo }) => {
    listaGeneros(jogo.genero).forEach((g) => contagem.set(g, (contagem.get(g) || 0) + 1));
  });

  const generos = [...contagem.entries()].sort((a, b) => b[1] - a[1]).slice(0, 12);
  document.querySelector("#chips-generos").innerHTML = generos.length
    ? generos
        .map(([g, n]) => `<a class="chip-genero" href="jogos.html?genero=${encodeURIComponent(g)}">${escaparHtml(g)} <span>${n}</span></a>`)
        .join("")
    : `<p class="demo-notice">Os gêneros aparecem aqui quando houver jogos cadastrados.</p>`;
}

// assistente de IA **ainda sem back-end
function configurarAssistente() {
  const form = document.querySelector("#ai-form");
  const pergunta = document.querySelector("#ai-question");
  const retorno = document.querySelector("#ai-feedback");
  form.addEventListener("submit", (evento) => {
    evento.preventDefault();
    retorno.textContent = pergunta.value.trim()
      ? "A interface está pronta para receber a integração com o serviço de IA. Ainda não enviamos sua pergunta a um servidor."
      : "Digite o nome de um jogo ou sua dúvida para testar este campo.";
  });
}
