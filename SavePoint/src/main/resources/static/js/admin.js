let listaJogosCache = [];
let listaLojasCache = [];

document.addEventListener("DOMContentLoaded", async () => {

  const usuario = await validarAcessoAdmin();
  if (!usuario) return;
  document.body.classList.remove("admin-protegido");

  configurarAbas();
  configurarEventosFormularios();
  await recarregarTudo();
});

async function validarAcessoAdmin() {
  const sessao = typeof obterSessao === "function" ? obterSessao() : null;

  if (!sessao || !sessao.token) {
    window.location.href = "login.html?motivo=admin_required";
    return null;
  }

  try {
    const usuario = await carregarUsuarioLogado();

    const temRoleAdmin = usuario && (
      usuario.role === "ADMIN" ||
      (Array.isArray(usuario.authorities) && usuario.authorities.some(a => a.authority === "ROLE_ADMIN"))
    );

    if (!temRoleAdmin) {
      const painelBloqueio = document.querySelector("#bloqueio-acesso");
      const conteudoAdmin = document.querySelector("#conteudo-admin");

      if (conteudoAdmin) conteudoAdmin.style.display = "none";
      if (painelBloqueio) painelBloqueio.hidden = false;

      setTimeout(() => {
        window.location.href = "index.html";
      }, 2500);

      return null;
    }

    return usuario;
  } catch (erro) {
    console.error("Falha ao checar permissão:", erro);
    window.location.href = "login.html?motivo=admin_required";
    return null;
  }
}

function configurarAbas() {
  const botoesAba = document.querySelectorAll(".abas .aba");
  botoesAba.forEach(btn => {
    btn.addEventListener("click", () => {
      botoesAba.forEach(b => b.classList.remove("ativa"));
      document.querySelectorAll(".tab-painel").forEach(p => p.classList.remove("ativo"));

      btn.classList.add("ativa");
      const target = btn.dataset.tab;
      const painel = document.querySelector(`#tab-${target}`);
      if (painel) painel.classList.add("ativo");
    });
  });
}

async function recarregarTudo() {
  await Promise.allSettled([
    carregarTabelaJogos(),
    carregarTabelaLojas(),
    carregarTabelaUsuarios()
  ]);
  popularSelects();
}

function exibirAviso(texto, tipo = "sucesso") {
  const alerta = document.querySelector("#admin-alerta");
  if (!alerta) return;
  alerta.className = `admin-mensagem ${tipo}`;
  alerta.textContent = texto;
  alerta.hidden = false;
  setTimeout(() => { alerta.hidden = true; }, 4000);
}

function configurarEventosFormularios() {
  const formJogo = document.querySelector("#form-cadastrar-jogo");
    if (formJogo) {
      formJogo.addEventListener("submit", async (e) => {
        e.preventDefault();

        const payloadJogo = {
          titulo: document.querySelector("#jogo-titulo").value.trim(),
          genero: document.querySelector("#jogo-genero").value.trim(),
          dataLancamento: document.querySelector("#jogo-lancamento").value,
          classificacaoIndicativa: document.querySelector("#jogo-classificacao").value,
          desenvolvedora: document.querySelector("#jogo-desenvolvedora").value.trim() || "Independente",
          imagemCapa: document.querySelector("#jogo-capa").value.trim() || null,
          descricao: document.querySelector("#jogo-descricao").value.trim() || "Sem descrição disponível."
        };

        const requisitos = {
          sistemaOperacional: document.querySelector("#req-so").value.trim(),
          processador: document.querySelector("#req-cpu").value.trim(),
          memoria: document.querySelector("#req-ram").value.trim(),
          placaDeVideo: document.querySelector("#req-gpu").value.trim()
        };

        const temRequisitos = Object.values(requisitos).some((valor) => valor !== "");

        try {
          const jogoCriado = await apiRequest("/jogos", {
            method: "POST",
            body: payloadJogo,
            autenticado: true
          });

          if (temRequisitos && jogoCriado && jogoCriado.id) {
            await apiRequest(`/jogos/${jogoCriado.id}/requisitos-minimos`, {
              method: "POST",
              body: requisitos,
              autenticado: true
            });
          }

          exibirAviso(temRequisitos ? "Jogo e requisitos mínimos cadastrados!" : "Jogo cadastrado com sucesso!");
          e.target.reset();
          await carregarTabelaJogos();
          popularSelects();
        } catch (erro) {
          exibirAviso(erro.message || "Erro ao cadastrar jogo.", "erro");
        }
      });
    }

  const formLoja = document.querySelector("#form-cadastrar-loja");
  if (formLoja) {
    formLoja.addEventListener("submit", async (e) => {
      e.preventDefault();
      const payload = {
        nome: document.querySelector("#loja-nome").value.trim(),
        urlLoja: document.querySelector("#loja-url").value.trim(),
        urlLogo: document.querySelector("#loja-logo").value.trim() || null
      };

      try {
        await apiRequest("/lojas", { method: "POST", body: payload, autenticado: true });
        exibirAviso("Loja cadastrada com sucesso!");
        e.target.reset();
        await carregarTabelaLojas();
        popularSelects();
      } catch (erro) {
        exibirAviso(erro.message || "Erro ao cadastrar loja.", "erro");
      }
    });
  }

  const formOferta = document.querySelector("#form-cadastrar-oferta");
  if (formOferta) {
    formOferta.addEventListener("submit", async (e) => {
      e.preventDefault();
      const jogoId = document.querySelector("#oferta-jogo-select").value;
      const lojaId = document.querySelector("#oferta-loja-select").value;
      const precoOriginal = Number(document.querySelector("#oferta-preco-orig").value);
      const precoAtual = Number(document.querySelector("#oferta-preco-atual").value);

       const payload = {
         precoOriginal,
         precoAtual,
         percentualDesconto: 0.0,
         loja: { id: Number(lojaId) }
       };

      try {
        await apiRequest(`/jogos/${jogoId}/ofertas`, { method: "POST", body: payload, autenticado: true });
        exibirAviso("Oferta publicada com sucesso!");
        e.target.reset();
        carregarAuditoriaOfertas(jogoId);
      } catch (erro) {
        exibirAviso(erro.message || "Erro ao vincular oferta.", "erro");
      }
    });
  }

    document.querySelector("#form-cadastrar-admin").addEventListener("submit", async (e) => {
      e.preventDefault();

      const payload = {
        nome: document.querySelector("#admin-nome").value.trim(),
        email: document.querySelector("#admin-email").value.trim(),
        senha: document.querySelector("#admin-senha").value
      };

      try {
        await apiRequest("/usuarios/admin", {
          method: "POST",
          body: payload,
          autenticado: true
        });

        exibirAviso("Novo Administrador registrado com sucesso!");
        e.target.reset();
        await carregarTabelaUsuarios();
      } catch (erro) {
        exibirAviso(erro.message || "Erro ao registrar administrador.", "erro");
      }
    });

  const filtroAudit = document.querySelector("#filtro-auditoria-jogo");
  if (filtroAudit) {
    filtroAudit.addEventListener("change", (e) => {
      if (e.target.value) carregarAuditoriaOfertas(e.target.value);
    });
  }

  const btnRecJogos = document.querySelector("#btn-recarregar-jogos");
  if (btnRecJogos) btnRecJogos.addEventListener("click", carregarTabelaJogos);

  const btnRecLojas = document.querySelector("#btn-recarregar-lojas");
  if (btnRecLojas) btnRecLojas.addEventListener("click", carregarTabelaLojas);

  const btnRecUsuarios = document.querySelector("#btn-recarregar-usuarios");
  if (btnRecUsuarios) btnRecUsuarios.addEventListener("click", carregarTabelaUsuarios);
}

async function carregarTabelaJogos() {
  const tbody = document.querySelector("#tabela-jogos-corpo");
  if (!tbody) return;

  try {
    tbody.innerHTML = `<tr><td colspan="4" class="estado-vazio">Carregando jogos...</td></tr>`;
    listaJogosCache = await apiRequest("/jogos");

    if (!Array.isArray(listaJogosCache) || listaJogosCache.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="estado-vazio">Nenhum jogo cadastrado.</td></tr>`;
      return;
    }

    tbody.innerHTML = listaJogosCache.map(j => `
      <tr>
        <td><strong>#${j.id}</strong></td>
        <td>${j.titulo || "-"}</td>
        <td>${j.genero || "-"}</td>
        <td>
          <button type="button" class="btn-acao-del" onclick="removerJogo(${j.id})">Excluir</button>
        </td>
      </tr>
    `).join("");
  } catch (erro) {
    tbody.innerHTML = `<tr><td colspan="4" class="estado-vazio">${erro.message}</td></tr>`;
  }
}

async function carregarTabelaLojas() {
  const tbody = document.querySelector("#tabela-lojas-corpo");
  if (!tbody) return;

  try {
    listaLojasCache = await apiRequest("/lojas");

    if (!Array.isArray(listaLojasCache) || listaLojasCache.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="estado-vazio">Nenhuma loja cadastrada.</td></tr>`;
      return;
    }

    tbody.innerHTML = listaLojasCache.map(l => `
      <tr>
        <td><strong>#${l.id}</strong></td>
        <td>${l.nome || "-"}</td>
        <td><a href="${l.urlLoja}" target="_blank" style="color: var(--lime); font-size: 11px;">Acessar</a></td>
        <td>
          <button type="button" class="btn-acao-del" onclick="removerLoja(${l.id})">Excluir</button>
        </td>
      </tr>
    `).join("");
  } catch (erro) {
    tbody.innerHTML = `<tr><td colspan="4" class="estado-vazio">${erro.message}</td></tr>`;
  }
}

async function carregarTabelaUsuarios() {
  const tbody = document.querySelector("#tabela-usuarios-corpo");
  if (!tbody) return;

  try {
    const usuarios = await apiRequest("/usuarios", { autenticado: true });

    if (!Array.isArray(usuarios) || usuarios.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" class="estado-vazio">Nenhum usuário encontrado.</td></tr>`;
      return;
    }

    tbody.innerHTML = usuarios.map(u => {
      const isAdmin = u.role === "ADMIN" ||
                      (Array.isArray(u.authorities) && u.authorities.some(a => a.authority === "ROLE_ADMIN"));
      return `
        <tr>
          <td><strong>#${u.id}</strong></td>
          <td>${u.nome || "-"}</td>
          <td>${u.email || "-"}</td>
          <td>
            <span class="badge-role ${isAdmin ? 'admin' : 'user'}">
              ${isAdmin ? 'ADMIN' : 'USER'}
            </span>
          </td>
          <td>
            <button type="button" class="btn-acao-del" onclick="removerUsuario(${u.id})">Excluir</button>
          </td>
        </tr>
      `;
    }).join("");
  } catch (erro) {
    tbody.innerHTML = `<tr><td colspan="5" class="estado-vazio">${erro.message}</td></tr>`;
  }
}

async function carregarAuditoriaOfertas(jogoId) {
      const tbody = document.querySelector("#tabela-ofertas-corpo");
      if (!tbody) return;

      tbody.innerHTML = `<tr><td colspan="5" class="estado-vazio">Carregando...</td></tr>`;

      try {
        const ofertas = await apiRequest(`/ofertas/${encodeURIComponent(jogoId)}/comparacao`);

        if (!Array.isArray(ofertas) || !ofertas.length) {
          tbody.innerHTML = `<tr><td colspan="5" class="estado-vazio">Nenhuma oferta cadastrada para este jogo.</td></tr>`;
          return;
        }

        tbody.innerHTML = ofertas
          .map((o) => `
            <tr>
              <td>${escaparHtml(o.loja?.nome || "Loja")}</td>
              <td>${formatarPreco(o.precoOriginal)}</td>
              <td><strong>${formatarPreco(o.precoAtual)}</strong></td>
              <td>${Math.round(o.percentualDesconto || 0)}%</td>
              <td>
               <div style="display:flex; gap:6px;">
                  <button type="button" class="botao-secundario botao-sm" onclick="alterarPrecoOferta(${o.id}, ${jogoId}, ${o.precoAtual})">
                     Alterar preço
                      </button>
                        <button type="button" class="botao-perigo botao-sm" onclick="excluirOferta(${o.id}, ${jogoId})">
                          Excluir
                        </button>
                     </div>
                  </td>
            </tr>
          `)
          .join("");
      } catch (erro) {
        tbody.innerHTML = `<tr><td colspan="5" class="estado-vazio">${escaparHtml(erro.message)}</td></tr>`;
      }
    }

    async function alterarPrecoOferta(ofertaId, jogoId, precoAtual) {
      const entrada = prompt(`Novo preço (R$) para esta oferta (atual: R$ ${precoAtual}):`, precoAtual);
      if (entrada === null) return;

      const novoPreco = Number(entrada.replace(",", "."));
      if (!Number.isFinite(novoPreco) || novoPreco < 0) {
        exibirAviso("Informe um preço válido.", "erro");
        return;
      }

      try {
        await apiRequest(`/ofertas/${ofertaId}/historico`, {
          method: "POST",
          body: { preco: novoPreco },
          autenticado: true,
        });
        exibirAviso("Preço atualizado e salvo no histórico!");
        carregarAuditoriaOfertas(jogoId);
      } catch (erro) {
        exibirAviso(erro.message || "Erro ao registrar alteração de preço.", "erro");
      }
    }

    async function excluirOferta(ofertaId, jogoId) {
      if (!confirm("Tem certeza que deseja excluir esta oferta e todo o histórico dela?")) return;

      try {
        await apiRequest(`/ofertas/${ofertaId}`, {
          method: "DELETE",
          autenticado: true,
        });
        exibirAviso("Oferta excluída com sucesso!");
        carregarAuditoriaOfertas(jogoId);
      } catch (erro) {
        exibirAviso(erro.message || "Erro ao excluir oferta.", "erro");
      }
}

function popularSelects() {
  const opcoesJogos = `<option value="">Selecione um jogo...</option>` +
    listaJogosCache.map((j) => `<option value="${j.id}">${escaparHtml(j.titulo)}</option>`).join("");

  const opcoesLojas = `<option value="">Selecione uma loja...</option>` +
    listaLojasCache.map((l) => `<option value="${l.id}">${escaparHtml(l.nome)}</option>`).join("");

  const selectJogoOferta = document.querySelector("#oferta-jogo-select");
  const selectFiltroJogo = document.querySelector("#filtro-auditoria-jogo");
  const selectLojaOferta = document.querySelector("#oferta-loja-select");

  if (selectJogoOferta) selectJogoOferta.innerHTML = opcoesJogos;
  if (selectFiltroJogo) selectFiltroJogo.innerHTML = opcoesJogos;
  if (selectLojaOferta) selectLojaOferta.innerHTML = opcoesLojas;
}

async function removerJogo(id) {
  if (!confirm("Tem certeza que deseja excluir este jogo?")) return;
  try {
    await apiRequest(`/jogos/${id}`, { method: "DELETE", autenticado: true });
    exibirAviso("Jogo excluído com sucesso!");
    await carregarTabelaJogos();
    popularSelects();
  } catch (erro) {
    exibirAviso(erro.message || "Erro ao excluir jogo.", "erro");
  }
}