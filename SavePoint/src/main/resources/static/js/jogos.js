// catálogo
let catalogoCompleto = [];

const campos = {};

document.addEventListener("DOMContentLoaded", () => {
  campos.busca = document.querySelector("#filtro-busca");
  campos.ordem = document.querySelector("#filtro-ordem");
  campos.promo = document.querySelector("#filtro-promo");
  campos.preco = document.querySelector("#filtro-preco");
  campos.generos = document.querySelector("#filtro-generos");
  campos.lista = document.querySelector("#lista-jogos");
  campos.contador = document.querySelector("#contador-resultados");

  // no celular os filtros começam fechados para os resultados aparecerem primeiro
  if (window.matchMedia("(max-width: 800px)").matches) {
    document.querySelector(".filtros details").removeAttribute("open");
  }

  lerFiltrosDaUrl();
  campos.lista.innerHTML = htmlSkeletonLinhas(8);

  let atraso;
  campos.busca.addEventListener("input", () => {
    clearTimeout(atraso);
    atraso = setTimeout(aplicarFiltros, 200);
  });
  [campos.ordem, campos.promo, campos.preco].forEach((c) => c.addEventListener("change", aplicarFiltros));
  campos.generos.addEventListener("change", aplicarFiltros);
  document.querySelector("#limpar-filtros").addEventListener("click", limparFiltros);

  carregar();
});

async function carregar() {
  try {
    catalogoCompleto = await carregarCatalogo();
  } catch (erro) {
    campos.lista.removeAttribute("aria-busy");
    campos.lista.innerHTML = `<p class="estado-vazio">${escaparHtml(erro.message)}</p>`;
    campos.contador.textContent = "";
    return;
  }
  montarFiltroGeneros();
  aplicarFiltros();
}

function lerFiltrosDaUrl() {
  const params = new URLSearchParams(window.location.search);
  campos.busca.value = params.get("q") || "";
  campos.ordem.value = params.get("ordem") || "relevancia";
  if (!campos.ordem.value) campos.ordem.value = "relevancia";
  campos.promo.checked = params.get("promo") === "1";
  campos.preco.value = params.get("max") || "";
}

function generosDaUrl() {
  return (parametroDaUrl("genero") || "").split("|").map((g) => g.trim()).filter(Boolean);
}

function montarFiltroGeneros() {
  const marcados = new Set(generosDaUrl().map((g) => g.toLocaleLowerCase("pt-BR")));
  const todos = new Set();
  catalogoCompleto.forEach(({ jogo }) => listaGeneros(jogo.genero).forEach((g) => todos.add(g)));

  const ordenados = [...todos].sort((a, b) => a.localeCompare(b, "pt-BR"));
  campos.generos.innerHTML = ordenados.length
    ? ordenados
        .map((g) => `
          <label class="campo-check">
            <input type="checkbox" value="${escaparHtml(g)}" ${marcados.has(g.toLocaleLowerCase("pt-BR")) ? "checked" : ""}>
            <span>${escaparHtml(g)}</span>
          </label>`)
        .join("")
    : `<p class="demo-notice">Nenhum gênero cadastrado.</p>`;
}

function generosMarcados() {
  return [...campos.generos.querySelectorAll("input:checked")].map((i) => i.value);
}

function normalizar(texto) {
  return String(texto || "")
    .toLocaleLowerCase("pt-BR")
    .normalize("NFD")
    .replace(/[̀-ͯ]/g, "");
}

function aplicarFiltros() {
  const busca = normalizar(campos.busca.value.trim());
  const generos = generosMarcados().map(normalizar);
  const somentePromo = campos.promo.checked;
  const precoMax = campos.preco.value ? Number(campos.preco.value) : null;
  const ordem = campos.ordem.value;

  let resultado = catalogoCompleto.filter(({ jogo, melhorOferta }) => {
    // a busca procura no nome, nos gêneros e na desenvolvedora
    const texto = normalizar(`${jogo.titulo} ${jogo.genero} ${jogo.desenvolvedora}`);
    if (busca && !texto.includes(busca)) return false;
    if (generos.length) {
      const doJogo = listaGeneros(jogo.genero).map(normalizar);
      if (!generos.some((g) => doJogo.includes(g))) return false;
    }
    if (somentePromo && !(melhorOferta && melhorOferta.percentualDesconto > 0)) return false;
    if (precoMax !== null && !(melhorOferta && melhorOferta.precoAtual <= precoMax)) return false;
    return true;
  });

  resultado = ordenar(resultado, ordem, busca);
  renderizarResultado(resultado);
  salvarFiltrosNaUrl();
}

function ordenar(lista, ordem, busca) {
  const copia = [...lista];
  const preco = (e) => (e.melhorOferta ? e.melhorOferta.precoAtual : Infinity);
  switch (ordem) {
    case "desconto":
      return copia.sort((a, b) => descontoDe(b) - descontoDe(a));
    case "preco":
      return copia.sort((a, b) => preco(a) - preco(b));
    case "preco-desc":
      return copia.sort((a, b) => (b.melhorOferta ? b.melhorOferta.precoAtual : -1) - (a.melhorOferta ? a.melhorOferta.precoAtual : -1));
    case "lancamento":
      return copia.sort((a, b) => String(b.jogo.dataLancamento || "").localeCompare(String(a.jogo.dataLancamento || "")));
    case "nome":
      return copia.sort((a, b) => String(a.jogo.titulo).localeCompare(String(b.jogo.titulo), "pt-BR"));
    default:
      // relevância: quem começa com o termo buscado vem antes, depois maior desconto
      return copia.sort((a, b) => {
        if (busca) {
          const aComeca = normalizar(a.jogo.titulo).startsWith(busca) ? 1 : 0;
          const bComeca = normalizar(b.jogo.titulo).startsWith(busca) ? 1 : 0;
          if (aComeca !== bComeca) return bComeca - aComeca;
        }
        return descontoDe(b) - descontoDe(a);
      });
  }
}

function renderizarResultado(lista) {
  campos.lista.removeAttribute("aria-busy");
  const total = lista.length;
  campos.contador.textContent = `${total} ${total === 1 ? "resultado" : "resultados"}`;

  campos.lista.innerHTML = total
    ? lista.map(htmlLinhaJogo).join("")
    : `<div class="estado-vazio">
         <strong>Nenhum jogo encontrado.</strong>
         <p>Tente outro nome ou remova algum filtro.</p>
       </div>`;
  atualizarBotoesWishlist();
}

function salvarFiltrosNaUrl() {
  const params = new URLSearchParams();
  if (campos.busca.value.trim()) params.set("q", campos.busca.value.trim());
  const generos = generosMarcados();
  if (generos.length) params.set("genero", generos.join("|"));
  if (campos.promo.checked) params.set("promo", "1");
  if (campos.preco.value) params.set("max", campos.preco.value);
  if (campos.ordem.value !== "relevancia") params.set("ordem", campos.ordem.value);
  const query = params.toString();
  history.replaceState(null, "", query ? `?${query}` : window.location.pathname);

  const buscaTopo = document.querySelector("#busca-topo");
  if (buscaTopo && document.activeElement !== buscaTopo) buscaTopo.value = campos.busca.value;
}

function limparFiltros() {
  campos.busca.value = "";
  campos.ordem.value = "relevancia";
  campos.promo.checked = false;
  campos.preco.value = "";
  campos.generos.querySelectorAll("input").forEach((i) => (i.checked = false));
  aplicarFiltros();
}
