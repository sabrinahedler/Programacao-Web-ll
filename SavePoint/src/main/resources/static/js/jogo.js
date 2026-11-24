const jogoId = Number(parametroDaUrl("id"));
let jogoAtual = null;
let ofertasAtuais = [];
let avaliacoesAtuais = [];

document.addEventListener("DOMContentLoaded", carregarPaginaJogo);

async function carregarPaginaJogo() {
  const pagina = document.querySelector("#pagina-jogo");

  if (!Number.isInteger(jogoId) || jogoId <= 0) {
    mostrarJogoInexistente(pagina);
    return;
  }

  try {
    const [jogo, ofertas, avaliacoes] = await Promise.all([
      SavePointApi.buscarJogo(jogoId),
      SavePointApi.ofertasDoJogo(jogoId).catch(() => []),
      SavePointApi.listarAvaliacoes(jogoId).catch(() => []),
    ]);
    jogoAtual = jogo;
    ofertasAtuais = Array.isArray(ofertas) ? ofertas : [];
    avaliacoesAtuais = Array.isArray(avaliacoes) ? avaliacoes : [];
  } catch (erro) {
    if (erro.status === 404) {
      mostrarJogoInexistente(pagina);
    } else {
      pagina.removeAttribute("aria-busy");
      pagina.innerHTML = `<p class="estado-vazio">${escaparHtml(erro.message)}</p>`;
    }
    return;
  }

  document.title = `${jogoAtual.titulo} | SavePoint`;
  pagina.removeAttribute("aria-busy");
  pagina.innerHTML = htmlPaginaJogo();

  configurarAbas();
  renderizarAcoesWishlist();
  renderizarAvaliacoes();
  document.addEventListener("wishlist:mudou", renderizarAcoesWishlist);
}

function mostrarJogoInexistente(pagina) {
  pagina.removeAttribute("aria-busy");
  pagina.innerHTML = `
    <div class="estado-vazio">
      <strong>Jogo não encontrado.</strong>
      <p>Ele pode ter sido removido do catálogo.</p>
      <a class="botao-primario" href="jogos.html">Ver todos os jogos</a>
    </div>`;
}

function htmlPaginaJogo() {
  const jogo = jogoAtual;
  const generos = listaGeneros(jogo.genero);
  const resumo = resumoAvaliacoes(avaliacoesAtuais);
  const primeiroGenero = generos[0];

  return `
    <nav class="breadcrumb" aria-label="Você está em">
      <a href="index.html">Início</a> <span>›</span>
      <a href="jogos.html">Jogos</a> <span>›</span>
      ${primeiroGenero ? `<a href="jogos.html?genero=${encodeURIComponent(primeiroGenero)}">${escaparHtml(primeiroGenero)}</a> <span>›</span>` : ""}
      ${escaparHtml(jogo.titulo)}
    </nav>
    <div class="jogo-cabecalho">
  <h1 class="jogo-titulo">${escaparHtml(jogo.titulo)}</h1>
  <div class="jogo-acoes" id="acoes-wishlist"></div>
</div>

    <div class="jogo-topo">
      <div class="jogo-midia">${htmlCapa(jogo, "capa-grande")}</div>
      <aside class="jogo-lateral">
        <p class="jogo-descricao-curta">${escaparHtml(jogo.descricao || "Descrição ainda não cadastrada.")}</p>
        <dl class="jogo-ficha">
          <div><dt>Avaliações</dt><dd id="ficha-avaliacoes">${htmlRotuloAvaliacao(resumo)}</dd></div>
          ${jogo.dataLancamento ? `<div><dt>Lançamento</dt><dd>${formatarData(jogo.dataLancamento)}</dd></div>` : ""}
          ${jogo.desenvolvedora ? `<div><dt>Desenvolvedora</dt><dd>${escaparHtml(jogo.desenvolvedora)}</dd></div>` : ""}
          ${jogo.classificacaoIndicativa ? `<div><dt>Classificação</dt><dd><span class="classificacao">${escaparHtml(jogo.classificacaoIndicativa)}</span></dd></div>` : ""}
        </dl>
        <div class="tags">
          ${generos.map((g) => `<a class="tag" href="jogos.html?genero=${encodeURIComponent(g)}">${escaparHtml(g)}</a>`).join("")}
        </div>
      </aside>
    </div>


    <section class="bloco" id="onde-comprar" aria-labelledby="titulo-comprar">
      <h2 id="titulo-comprar">Onde comprar ${escaparHtml(jogo.titulo)}</h2>
      ${htmlOfertas()}
    </section>

    <div class="abas" role="tablist" aria-label="Informações do jogo">
          <button class="aba ativa" type="button" role="tab" id="aba-sobre" aria-controls="painel-sobre" aria-selected="true">Sobre o jogo</button>
          <button class="aba" type="button" role="tab" id="aba-requisitos" aria-controls="painel-requisitos" aria-selected="false">Requisitos mínimos</button>
          <button class="aba" type="button" role="tab" id="aba-avaliacoes" aria-controls="painel-avaliacoes" aria-selected="false">Avaliações <span class="contador-aba" id="contador-avaliacoes">${resumo.total}</span></button>
          <button class="aba" type="button" role="tab" id="aba-historico" aria-controls="painel-historico" aria-selected="false">Histórico de preços</button>
        </div>

        <section class="bloco painel-aba" role="tabpanel" id="painel-sobre" aria-labelledby="aba-sobre">
          ${htmlDescricaoCompleta(jogo.descricao)}
        </section>

        <section class="bloco painel-aba" role="tabpanel" id="painel-requisitos" aria-labelledby="aba-requisitos" hidden>
          ${htmlRequisitos(jogo.requisitosMinimos)}
        </section>

        <section class="bloco painel-aba" role="tabpanel" id="painel-avaliacoes" aria-labelledby="aba-avaliacoes" hidden>
          <div id="resumo-avaliacoes"></div>
          <div id="area-formulario-avaliacao"></div>
          <div id="lista-avaliacoes"></div>
        </section>

        <section class="bloco painel-aba" role="tabpanel" id="painel-historico" aria-labelledby="aba-historico" hidden>
          <div class="historico-topo">
            <div>
              <h3>Histórico de preços</h3>
              <p class="demo-notice" id="resumo-historico">Selecione uma loja para ver a variação.</p>
            </div>

            <select id="select-loja-historico" aria-label="Escolher loja">
              <option value="">Carregando lojas...</option>
            </select>
          </div>

          <div class="grafico-container">
            <canvas id="grafico-historico"></canvas>
          </div>
          <p id="historico-vazio" class="estado-vazio" hidden>
            Ainda não há registros de alteração de preço para esta oferta.
          </p>
        </section>`;
    }

function htmlRotuloAvaliacao(resumo) {
  if (!resumo.total) return `<span class="rotulo-avaliacao neutro">Sem avaliações</span>`;
  return `<span class="rotulo-avaliacao ${resumo.classe}">${resumo.rotulo}</span> <small>(${resumo.total})</small>`;
}

function htmlDescricaoCompleta(descricao) {
  if (!descricao) return `<p class="estado-vazio">Descrição ainda não cadastrada.</p>`;
  return String(descricao)
    .split(/\n+/)
    .map((paragrafo) => `<p>${escaparHtml(paragrafo)}</p>`)
    .join("");
}

function htmlRequisitos(req) {
  if (!req) return `<p class="estado-vazio">Requisitos mínimos ainda não cadastrados.</p>`;
  const linhas = [
    ["Sistema operacional", req.sistemaOperacional],
    ["Processador", req.processador],
    ["Memória", req.memoria],
    ["Placa de vídeo", req.placaDeVideo],
  ].filter(([, valor]) => valor);
  return `
    <dl class="tabela-requisitos">
      ${linhas.map(([rotulo, valor]) => `<div><dt>${rotulo}</dt><dd>${escaparHtml(valor)}</dd></div>`).join("")}
    </dl>`;
}


// onde comprar
function htmlOfertas() {
  if (!ofertasAtuais.length) {
    return `<p class="estado-vazio">Nenhuma loja com oferta cadastrada para este jogo ainda.</p>`;
  }

  // Ordena as ofertas da mais barata para a mais cara.
  const ofertasOrdenadas = [...ofertasAtuais].sort(
    (a, b) => Number(a.precoAtual) - Number(b.precoAtual)
  );

  return `
    <div class="lista-ofertas">
      ${ofertasOrdenadas
        .map((oferta, indice) => {
          const loja = oferta.loja || {};
          const nome = loja.nome || "Loja";
          const link = urlExternaSegura(loja.urlLoja);
          const logo = urlExternaSegura(loja.urlLogo);

          return `
            <div class="oferta${indice === 0 ? " melhor" : ""}">
              <div class="oferta-loja">
                <span class="loja-logo" aria-hidden="true">${escaparHtml(nome.charAt(0).toUpperCase())}${logo ? `<img src="${escaparHtml(logo)}" alt="" data-logo-loja="1" loading="lazy">` : ""}</span>
                <div>
                  <strong>${escaparHtml(nome)}</strong>
                  ${indice === 0 ? `<span class="selo-melhor">Menor preço</span>` : ""}
                </div>
              </div>

              <div class="oferta-preco">${htmlPreco(oferta)}</div>

              ${link
                ? `<a class="botao-loja" href="${escaparHtml(link)}" target="_blank" rel="noopener noreferrer">Ir para a loja ${ICONES.externo}<span class="sr-only"> (abre em nova aba)</span></a>`
                : `<span class="botao-loja desabilitado">Link indisponível</span>`}
            </div>`;
        })
        .join("")}
    </div>

    <p class="demo-notice">A compra é feita no site da loja. Preços podem mudar sem aviso.</p>`;
}

let graficoHistoricoInstancia = null;
let historicoInicializado = false;

function inicializarHistorico() {
  historicoInicializado = true;
  const select = document.querySelector("#select-loja-historico");
  const avisoVazio = document.querySelector("#historico-vazio");
  const canvas = document.querySelector("#grafico-historico");

  if (!ofertasAtuais.length) {
    select.innerHTML = `<option value="">Sem ofertas cadastradas</option>`;
    canvas.hidden = true;
    avisoVazio.hidden = false;
    return;
  }

  const ordenadas = [...ofertasAtuais].sort(
    (a, b) => Number(a.precoAtual) - Number(b.precoAtual)
  );

  select.innerHTML = ordenadas
    .map((o) => {
      const nomeLoja = o.loja?.nome || `Oferta #${o.id}`;
      return `<option value="${o.id}">${escaparHtml(nomeLoja)}</option>`;
    })
    .join("");

  select.addEventListener("change", () => carregarHistoricoDaOferta(select.value));
  carregarHistoricoDaOferta(ordenadas[0].id);
}

async function carregarHistoricoDaOferta(ofertaId) {
  const canvas = document.querySelector("#grafico-historico");
  const avisoVazio = document.querySelector("#historico-vazio");
  const resumo = document.querySelector("#resumo-historico");

  try {
    const registros = await SavePointApi.historicoOferta(ofertaId);

    if (!Array.isArray(registros) || !registros.length) {
      if (graficoHistoricoInstancia) graficoHistoricoInstancia.destroy();
      canvas.hidden = true;
      avisoVazio.hidden = false;
      resumo.textContent = "Sem alterações de preço registradas.";
      return;
    }

    canvas.hidden = false;
    avisoVazio.hidden = true;

    const ordenados = [...registros].reverse();
    const datas = ordenados.map((r) =>
      new Date(r.data).toLocaleDateString("pt-BR", { day: "2-digit", month: "2-digit" })
    );
    const precos = ordenados.map((r) => Number(r.preco));

    const menorPreco = Math.min(...precos);
    resumo.innerHTML = `Menor preço registrado: <strong class="historico-menor-preco">${formatarPreco(menorPreco)}</strong>`;

    renderizarGraficoHistorico(canvas, datas, precos);
  } catch {
    resumo.textContent = "Não foi possível carregar o histórico.";
  }
}

function renderizarGraficoHistorico(canvas, labels, valores) {
  if (graficoHistoricoInstancia) graficoHistoricoInstancia.destroy();

  // Lê as cores direto das variáveis do seu style.css
  const estilos = getComputedStyle(document.documentElement);
  const corLinha = estilos.getPropertyValue("--lime").trim() || "#A3E635";
  const corPonto = estilos.getPropertyValue("--purple").trim() || "#7C3AED";
  const corTexto = estilos.getPropertyValue("--muted").trim() || "#9CA3AF";
  const corGrade = estilos.getPropertyValue("--line").trim() || "rgba(255, 255, 255, 0.08)";

  graficoHistoricoInstancia = new Chart(canvas, {
    type: "line",
    data: {
      labels,
      datasets: [
        {
          label: "Preço (R$)",
          data: valores,
          borderColor: corLinha,
          backgroundColor: "rgba(163, 230, 53, 0.12)",
          borderWidth: 2,
          pointBackgroundColor: corPonto,
          pointRadius: 4,
          tension: 0.2,
          fill: true,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx) => formatarPreco(ctx.raw),
          },
        },
      },
      scales: {
        x: {
          ticks: { color: corTexto },
          grid: { color: corGrade },
        },
        y: {
          beginAtZero: false,
          ticks: {
            color: corTexto,
            callback: (v) => `R$ ${v}`,
          },
          grid: { color: corGrade },
        },
      },
    },
  });
}

// lista de desejos
function renderizarAcoesWishlist() {
  const area = document.querySelector("#acoes-wishlist");
  if (!area || !jogoAtual) return;

  const item = ListaDesejos.item(jogoAtual.id);
  let resumoAlerta = "";
  if (item) {
    resumoAlerta = !item.notificarOferta
      ? "Sem aviso por e-mail."
      : item.precoAlerta > 0
        ? `Aviso quando custar até <strong>${formatarPreco(item.precoAlerta)}</strong>.`
        : "Aviso em qualquer promoção.";
  }

  area.innerHTML = `
    <button class="wish-btn wish-btn-texto" type="button" data-acao="wishlist" data-jogo-id="${jogoAtual.id}"
            data-titulo="${escaparHtml(jogoAtual.titulo)}" aria-pressed="false">
      <span class="icone-vazio">${ICONES.coracao}</span><span class="icone-cheio">${ICONES.coracaoCheio}</span>
      <span class="wish-texto">Adicionar à lista de desejos</span>
    </button>
    ${item ? `
      <p class="resumo-alerta">${ICONES.sino} ${resumoAlerta}</p>
      <button class="botao-link" type="button" id="editar-alerta">Editar alerta</button>` : ""}`;

  area.querySelector("#editar-alerta")?.addEventListener("click", () => {
    abrirJanelaAlerta({
      jogoId: jogoAtual.id,
      titulo: jogoAtual.titulo,
      precoAtual: ofertasAtuais[0] ? ofertasAtuais[0].precoAtual : null,
    });
  });
  atualizarBotoesWishlist();
}

function configurarAbas() {
  const abas = [...document.querySelectorAll('.abas[aria-label="Informações do jogo"] .aba')];
  const ativar = (aba) => {
    abas.forEach((a) => {
      const ativa = a === aba;
      a.classList.toggle("ativa", ativa);
      a.setAttribute("aria-selected", String(ativa));
      document.querySelector(`#${a.getAttribute("aria-controls")}`).hidden = !ativa;
    });

    if (aba.id === "aba-historico") {
      if (!historicoInicializado) {
        inicializarHistorico();
      } else if (graficoHistoricoInstancia) {
        graficoHistoricoInstancia.resize();
      }
    }
  };
  abas.forEach((aba) => aba.addEventListener("click", () => ativar(aba)));

  // setas do teclado trocam de aba - padrão de acessibilidade
  abas.forEach((aba, i) =>
    aba.addEventListener("keydown", (evento) => {
      if (evento.key !== "ArrowRight" && evento.key !== "ArrowLeft") return;
      const proxima = abas[(i + (evento.key === "ArrowRight" ? 1 : abas.length - 1)) % abas.length];
      proxima.focus();
      ativar(proxima);
    })
  );

  if (window.location.hash === "#avaliacoes") ativar(document.querySelector("#aba-avaliacoes"));
}

// avaliações
async function renderizarAvaliacoes() {
  const resumo = resumoAvaliacoes(avaliacoesAtuais);
  document.querySelector("#contador-avaliacoes").textContent = resumo.total;
  document.querySelector("#ficha-avaliacoes").innerHTML = htmlRotuloAvaliacao(resumo);

  document.querySelector("#resumo-avaliacoes").innerHTML = resumo.total
    ? `
      <div class="resumo-avaliacoes">
        <div class="resumo-nota"><strong>${resumo.media.toFixed(1).replace(".", ",")}</strong>${htmlEstrelas(resumo.media)}</div>
        <div>
          <p>${htmlRotuloAvaliacao(resumo)}</p>
          <p class="demo-notice">${resumo.percentual}% das ${resumo.total} avaliações recomendam este jogo (nota 4 ou 5).</p>
        </div>
      </div>`
    : "";

  const ordenadas = [...avaliacoesAtuais].sort((a, b) => String(b.dataPublicacao || "").localeCompare(String(a.dataPublicacao || "")));
  document.querySelector("#lista-avaliacoes").innerHTML = ordenadas.length
    ? ordenadas.map(htmlAvaliacao).join("")
    : `<p class="estado-vazio">Sem registro de avaliações</p>`;

  await renderizarFormularioAvaliacao();
}

function htmlAvaliacao(avaliacao) {
  const autor = avaliacao.usuario || {};
  const nota = Number(avaliacao.nota) || 0;
  const horas = Number(avaliacao.horasJogadas) || 0;

  let statusSelo = { classe: "nao", texto: "👎 Não recomendado" };
  if (nota >= 4) {
    statusSelo = { classe: "sim", texto: "👍 Recomendado" };
  } else if (nota === 3) {
    statusSelo = { classe: "neutro", texto: "⚖️ Neutro" };
  }

  return `
    <article class="avaliacao">
      <div class="avaliacao-autor">
        ${htmlAvatar(autor, "avatar avatar-avaliacao")}
        <div>
          <strong>${escaparHtml(autor.nome || "Jogador")}</strong>
          ${horas ? `<small>${horas.toLocaleString("pt-BR")} h jogadas</small>` : ""}
        </div>
      </div>
      <div class="avaliacao-corpo">
        <div class="avaliacao-topo">
          <span class="selo-recomenda ${statusSelo.classe}">${statusSelo.texto}</span>
          ${htmlEstrelas(avaliacao.nota)}
        </div>
        ${avaliacao.dataPublicacao ? `<p class="avaliacao-data">Publicada em ${formatarData(avaliacao.dataPublicacao)}</p>` : ""}
        <p class="avaliacao-texto">${escaparHtml(avaliacao.textoAvaliacao || "")}</p>
      </div>
    </article>`;
}

async function renderizarFormularioAvaliacao() {
  const area = document.querySelector("#area-formulario-avaliacao");

  if (!estaLogado()) {
    const voltar = `jogo.html?id=${jogoAtual.id}#avaliacoes`;
    area.innerHTML = `
      <div class="convite">
        <p>Jogou ${escaparHtml(jogoAtual.titulo)}? Conte para a comunidade.</p>
        <a class="botao-primario" href="login.html?motivo=avaliar&voltar=${encodeURIComponent(voltar)}">Entrar para avaliar</a>
      </div>`;
    return;
  }

  const usuario = await obterUsuario();
  const jaAvaliou = usuario && avaliacoesAtuais.some((a) => a.usuario && a.usuario.id === usuario.id);
  if (jaAvaliou) {
    area.innerHTML = `<p class="convite">Você já avaliou este jogo. Obrigado por contribuir!</p>`;
    return;
  }

  area.innerHTML = `
    <form class="form-avaliacao" id="form-avaliacao" novalidate>
      <h3>Escreva sua avaliação</h3>
      <fieldset class="seletor-estrelas">
        <legend>Sua nota</legend>
        <div class="estrelas-opcoes">
          ${[5, 4, 3, 2, 1]
            .map((n) => `
              <input type="radio" name="nota" id="nota-${n}" value="${n}" required>
              <label for="nota-${n}" title="${n} de 5"><span class="sr-only">${n} ${n === 1 ? "estrela" : "estrelas"}</span>★</label>`)
            .join("")}
        </div>
        <small>Notas 4 e 5 contam como "Recomendado".</small>
      </fieldset>
      <div class="campo">
        <label for="horas-jogadas">Horas jogadas <small>(opcional)</small></label>
        <input id="horas-jogadas" type="number" min="0" max="100000" step="1" inputmode="numeric" placeholder="Ex.: 40">
      </div>
      <div class="campo">
        <label for="texto-avaliacao">Comentário</label>
        <textarea id="texto-avaliacao" rows="5" maxlength="2000" placeholder="O que você achou do jogo?" required></textarea>
        <small id="contador-texto">0 / 2000</small>
      </div>
      <p class="form-erro" hidden></p>
      <button class="botao-primario" type="submit">Publicar avaliação</button>
    </form>`;

  const form = area.querySelector("#form-avaliacao");
  const texto = form.querySelector("#texto-avaliacao");
  const contador = form.querySelector("#contador-texto");
  const erro = form.querySelector(".form-erro");
  texto.addEventListener("input", () => (contador.textContent = `${texto.value.length} / 2000`));

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    erro.hidden = true;

    const nota = Number(form.querySelector('input[name="nota"]:checked')?.value || 0);
    const comentario = texto.value.trim();
    const horas = Number(form.querySelector("#horas-jogadas").value || 0);

    if (!nota) return mostrarErroForm(erro, "Escolha uma nota de 1 a 5 estrelas.");
    if (comentario.length < 5) return mostrarErroForm(erro, "Escreva um comentário com pelo menos 5 caracteres.");
    if (!Number.isInteger(horas) || horas < 0) return mostrarErroForm(erro, "Informe as horas jogadas como um número inteiro.");

    const botao = form.querySelector('[type="submit"]');
    botao.disabled = true;
    try {
      const nova = await SavePointApi.avaliar(jogoAtual.id, { nota, textoAvaliacao: comentario, horasJogadas: horas });
      // Garante que o nome e a foto do autor apareçam mesmo se a resposta vier sem eles.
      nova.usuario = { ...(await obterUsuario()), ...(nova.usuario || {}) };
      avaliacoesAtuais.push(nova);
      mostrarAviso("Avaliação publicada. Obrigado!", "sucesso");
      renderizarAvaliacoes();
    } catch (falha) {
      if (falha.status === 401) {
        irParaLogin("avaliar", `jogo.html?id=${jogoAtual.id}#avaliacoes`);
        return;
      }
      mostrarErroForm(erro, falha.message);
      botao.disabled = false;
    }
  });
}

function mostrarErroForm(elemento, mensagem) {
  elemento.textContent = mensagem;
  elemento.hidden = false;
}
