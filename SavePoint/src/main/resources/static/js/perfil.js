if (!estaLogado()) {
  irParaLogin("perfil", "perfil.html");
}

let usuarioPerfil = null;
let fotoEscolhida = null;

document.addEventListener("DOMContentLoaded", async () => {
  if (!estaLogado()) return;

  const area = document.querySelector("#perfil");
  try {
    usuarioPerfil = await SavePointApi.meusDados();
    atualizarUsuarioEmCache(usuarioPerfil);
  } catch (erro) {
    if (erro.status === 401) {
      irParaLogin("perfil", "perfil.html");
      return;
    }
    area.removeAttribute("aria-busy");
    area.innerHTML = `<p class="estado-vazio">${escaparHtml(erro.message)}</p>`;
    return;
  }

  renderizarPerfil();
  configurarJanelaAvatar();

  // quantidade de jogos na lista de desejos
  ListaDesejos.carregar().then(atualizarContadorLista).catch(() => {});
  document.addEventListener("wishlist:mudou", atualizarContadorLista);
});

function renderizarPerfil() {
  const area = document.querySelector("#perfil");
  const u = usuarioPerfil;
  area.removeAttribute("aria-busy");
  area.innerHTML = `
    <div class="perfil-cartao">
      <div class="perfil-foto">
        ${htmlAvatar(u, "avatar avatar-grande")}
        <button class="botao-secundario pequeno" type="button" id="trocar-foto">Alterar foto</button>
      </div>
      <div class="perfil-dados">
        <p class="section-kicker">MEU PERFIL</p>
        <h1>${escaparHtml(u.nome)}</h1>
        <dl class="perfil-ficha">
          <div><dt>E-mail</dt><dd>${escaparHtml(u.email)}</dd></div>
          ${u.dataCadastro ? `<div><dt>Membro desde</dt><dd>${formatarData(u.dataCadastro)}</dd></div>` : ""}
          <div><dt>Lista de desejos</dt><dd id="perfil-qtd-lista">—</dd></div>
        </dl>
        <div class="perfil-acoes">
          <a class="botao-primario" href="wishlist.html">Ver minha lista de desejos</a>
          <button class="botao-link perigo" type="button" id="sair-perfil">Sair da conta</button>
        </div>
      </div>
    </div>`;

  area.querySelector("#trocar-foto").addEventListener("click", abrirGaleria);
  area.querySelector("#sair-perfil").addEventListener("click", () => {
    limparSessao();
    window.location.href = "index.html";
  });
}

function atualizarContadorLista() {
  const campo = document.querySelector("#perfil-qtd-lista");
  if (!campo || !ListaDesejos.carregada) return;
  const n = ListaDesejos.itens.size;
  campo.textContent = `${n} ${n === 1 ? "jogo" : "jogos"}`;
}

// fotos de perfil
function configurarJanelaAvatar() {
  const janela = document.querySelector("#janela-avatar");
  janela.querySelectorAll("[data-fechar]").forEach((b) => b.addEventListener("click", () => janela.close()));
  document.querySelector("#salvar-avatar").addEventListener("click", salvarFoto);

  document.querySelector("#galeria-avatares").addEventListener("click", (evento) => {
    const opcao = evento.target.closest(".opcao-avatar");
    if (!opcao) return;
    marcarOpcao(opcao.dataset.foto);
  });
}

function abrirGaleria() {
  const janela = document.querySelector("#janela-avatar");
  const galeria = document.querySelector("#galeria-avatares");
  janela.querySelector(".janela-erro").hidden = true;

  // A foto padrão é sempre a primeira opção; depois vêm as fotos cadastradas em config.js.
  const opcoes = [{ arquivo: "", nome: "Foto padrão" }, ...(typeof AVATARES !== "undefined" ? AVATARES : [])];
  galeria.innerHTML = opcoes
    .map((opcao) => {
      const src = opcao.arquivo || AVATAR_PADRAO;
      return `
        <button class="opcao-avatar" type="button" role="radio" aria-checked="false" data-foto="${escaparHtml(opcao.arquivo)}">
          <img src="${escaparHtml(src)}" alt="" data-avatar="1">
          <span>${escaparHtml(opcao.nome || "")}</span>
        </button>`;
    })
    .join("");

  if (opcoes.length === 1) {
    galeria.insertAdjacentHTML(
      "beforeend",
      `<p class="demo-notice galeria-aviso">Ainda não há fotos na galeria. Elas aparecem aqui assim que forem adicionadas ao site.</p>`
    );
  }

  marcarOpcao(usuarioPerfil.fotoPerfil || "");
  janela.showModal();
}

function marcarOpcao(foto) {
  fotoEscolhida = foto;
  document.querySelectorAll(".opcao-avatar").forEach((opcao) => {
    const marcada = opcao.dataset.foto === foto;
    opcao.classList.toggle("marcada", marcada);
    opcao.setAttribute("aria-checked", String(marcada));
  });
}

async function salvarFoto() {
  const janela = document.querySelector("#janela-avatar");
  const erro = janela.querySelector(".janela-erro");
  const botao = document.querySelector("#salvar-avatar");

  botao.disabled = true;
  try {
    // texto vazio = voltar para a foto padrão - o servidor grava como null
    usuarioPerfil = await SavePointApi.atualizarUsuario(usuarioPerfil.id, { fotoPerfil: fotoEscolhida || "" });
    atualizarUsuarioEmCache(usuarioPerfil);
    renderizarPerfil();
    atualizarContadorLista();
    renderizarAcoesUsuario(usuarioPerfil); // atualiza a foto do cabeçalho
    janela.close();
    mostrarAviso("Foto de perfil atualizada.", "sucesso");
  } catch (falha) {
    erro.textContent = falha.message;
    erro.hidden = false;
  } finally {
    botao.disabled = false;
  }
}
