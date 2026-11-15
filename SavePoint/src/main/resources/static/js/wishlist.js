if (!estaLogado()) {
  irParaLogin("wishlist", "wishlist.html");
}

const ofertasPorJogo = new Map(); // jogoId -> melhor oferta (ou null)

document.addEventListener("DOMContentLoaded", async () => {
  if (!estaLogado()) return;

  const lista = document.querySelector("#lista-wishlist");
  lista.innerHTML = htmlSkeletonLinhas(4);

  try {
    const usuario = await obterUsuario();
    if (usuario) document.querySelector("#titulo-wishlist").textContent = `Lista de desejos de ${usuario.nome}`;

    await ListaDesejos.carregar();
    await carregarPrecos();
  } catch (erro) {
    lista.removeAttribute("aria-busy");
    lista.innerHTML = `<p class="estado-vazio">${escaparHtml(erro.message)}</p>`;
    document.querySelector("#contador-wishlist").textContent = "";
    return;
  }

  document.querySelector("#busca-wishlist").addEventListener("input", renderizarWishlist);
  document.querySelector("#ordem-wishlist").addEventListener("change", renderizarWishlist);
  lista.addEventListener("submit", salvarAlerta);
  lista.addEventListener("click", tratarCliqueLista);
  lista.addEventListener("change", alternarCampoPreco);

  renderizarWishlist();
});

// preço de hoje de cada jogo da lista
async function carregarPrecos() {
  const ids = [...ListaDesejos.itens.keys()];
  const resultados = await Promise.all(ids.map((id) => SavePointApi.ofertasDoJogo(id).catch(() => [])));
  ids.forEach((id, i) => ofertasPorJogo.set(id, (resultados[i] || [])[0] || null));
}

function itensOrdenados() {
  const busca = document.querySelector("#busca-wishlist").value.trim().toLocaleLowerCase("pt-BR");
  const ordem = document.querySelector("#ordem-wishlist").value;

  let itens = [...ListaDesejos.itens.values()].filter((item) =>
    String(item.jogo.titulo || "").toLocaleLowerCase("pt-BR").includes(busca)
  );

  const oferta = (item) => ofertasPorJogo.get(item.jogo.id);
  const preco = (item) => (oferta(item) ? oferta(item).precoAtual : Infinity);
  const desconto = (item) => (oferta(item) ? oferta(item).percentualDesconto || 0 : 0);

  if (ordem === "desconto") itens.sort((a, b) => desconto(b) - desconto(a));
  else if (ordem === "preco") itens.sort((a, b) => preco(a) - preco(b));
  else if (ordem === "nome") itens.sort((a, b) => String(a.jogo.titulo).localeCompare(String(b.jogo.titulo), "pt-BR"));
  else itens.sort((a, b) => b.id - a.id); // id maior = adicionado por último
  return itens;
}

function renderizarWishlist() {
  const lista = document.querySelector("#lista-wishlist");
  const total = ListaDesejos.itens.size;
  lista.removeAttribute("aria-busy");
  document.querySelector("#contador-wishlist").textContent =
    total ? `${total} ${total === 1 ? "jogo" : "jogos"} na lista` : "";
  document.querySelector("#ferramentas-wishlist").hidden = total === 0;

  if (!total) {
    lista.innerHTML = `
      <div class="estado-vazio">
        <span class="estado-vazio-icone">${ICONES.coracao}</span>
        <strong>Sua lista de desejos está vazia.</strong>
        <p>Toque no coração de qualquer jogo para salvá-lo aqui e receber um aviso quando o preço cair.</p>
        <a class="botao-primario" href="jogos.html">Explorar jogos</a>
      </div>`;
    return;
  }

  const itens = itensOrdenados();
  lista.innerHTML = itens.length
    ? itens.map(htmlItemWishlist).join("")
    : `<p class="estado-vazio">Nenhum jogo da sua lista corresponde à busca.</p>`;
}

function htmlItemWishlist(item) {
  const jogo = item.jogo;
  const melhor = ofertasPorJogo.get(jogo.id);
  const loja = melhor && melhor.loja ? melhor.loja.nome : "";
  const atingiu = melhor && item.precoAlerta > 0 && melhor.precoAtual <= item.precoAlerta;
  const generos = listaGeneros(jogo.genero).map(escaparHtml).join(", ");

  return `
    <article class="wish-item" data-jogo-id="${jogo.id}">
      <a class="wish-capa" href="jogo.html?id=${jogo.id}" tabindex="-1">${htmlCapa(jogo, "capa-linha")}</a>
      <div class="wish-info">
        <h3><a href="jogo.html?id=${jogo.id}">${escaparHtml(jogo.titulo)}</a></h3>
        <p class="game-tags">${generos || "Gênero não informado"}</p>
        ${jogo.dataLancamento ? `<p class="game-row-data">Lançamento: ${formatarData(jogo.dataLancamento)}</p>` : ""}
        <form class="wish-alerta" data-jogo-id="${jogo.id}">
          <label class="campo-check">
            <input type="checkbox" name="notificar" ${item.notificarOferta ? "checked" : ""}>
            <span>Avisar por e-mail</span>
          </label>
          <label class="wish-alerta-preco">
            <span>quando custar até</span>
            <span class="campo-moeda pequeno"><span>R$</span><input type="number" name="preco" min="0" step="0.01" inputmode="decimal"
              placeholder="qualquer promoção" value="${item.precoAlerta > 0 ? item.precoAlerta : ""}" ${item.notificarOferta ? "" : "disabled"}></span>
          </label>
          <button class="botao-secundario pequeno" type="submit">Salvar</button>
          <span class="wish-status" aria-live="polite"></span>
        </form>
      </div>
      <div class="wish-preco">
        ${atingiu ? `<span class="selo-alvo">Chegou no seu preço!</span>` : ""}
        ${htmlPreco(melhor)}
        ${loja ? `<p class="store-name">em <strong>${escaparHtml(loja)}</strong></p>` : ""}
        <div class="wish-botoes">
          <a class="botao-loja" href="jogo.html?id=${jogo.id}">Ver lojas</a>
          <button class="botao-link perigo" type="button" data-remover="${jogo.id}">Remover</button>
        </div>
      </div>
    </article>`;
}

function alternarCampoPreco(evento) {
  if (evento.target.name !== "notificar") return;
  const form = evento.target.closest("form");
  form.querySelector('[name="preco"]').disabled = !evento.target.checked;
}

async function salvarAlerta(evento) {
  const form = evento.target.closest(".wish-alerta");
  if (!form) return;
  evento.preventDefault();

  const jogoId = Number(form.dataset.jogoId);
  const status = form.querySelector(".wish-status");
  const campoPreco = form.querySelector('[name="preco"]');
  const notificar = form.querySelector('[name="notificar"]').checked;
  const preco = campoPreco.value === "" ? 0 : Number(campoPreco.value);

  if (!Number.isFinite(preco) || preco < 0) {
    status.textContent = "Valor inválido.";
    status.className = "wish-status erro";
    return;
  }

  const botao = form.querySelector('[type="submit"]');
  botao.disabled = true;
  status.textContent = "Salvando...";
  status.className = "wish-status";
  try {
    await ListaDesejos.atualizar(jogoId, preco, notificar);
    status.textContent = "Salvo ✓";
    status.className = "wish-status ok";
    // Atualiza o selo "Chegou no seu preço!" sem perder o foco do formulário.
    const melhor = ofertasPorJogo.get(jogoId);
    const cartao = form.closest(".wish-item");
    const atingiu = melhor && preco > 0 && melhor.precoAtual <= preco;
    const selo = cartao.querySelector(".selo-alvo");
    if (atingiu && !selo) cartao.querySelector(".wish-preco").insertAdjacentHTML("afterbegin", `<span class="selo-alvo">Chegou no seu preço!</span>`);
    if (!atingiu && selo) selo.remove();
  } catch (erro) {
    status.textContent = erro.message;
    status.className = "wish-status erro";
  } finally {
    botao.disabled = false;
  }
}

async function tratarCliqueLista(evento) {
  const botao = evento.target.closest("[data-remover]");
  if (!botao) return;

  const jogoId = Number(botao.dataset.remover);
  const cartao = botao.closest(".wish-item");
  botao.disabled = true;
  try {
    cartao.classList.add("saindo");
    await ListaDesejos.remover(jogoId);
    mostrarAviso("Removido da sua lista de desejos.");
    setTimeout(renderizarWishlist, 220);
  } catch (erro) {
    cartao.classList.remove("saindo");
    botao.disabled = false;
    mostrarAviso(erro.message, "erro");
  }
}
