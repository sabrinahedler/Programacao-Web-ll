const NICKNAME_VALIDO = /^[\p{L}0-9_.-]{3,30}$/u;

const MENSAGENS_MOTIVO = {
  wishlist: "Você precisa estar logado em uma conta para acessar a lista de desejos.",
  perfil: "Entre na sua conta para ver o seu perfil.",
  avaliar: "Entre na sua conta para avaliar jogos.",
};

// só volta para páginas do próprio site
function destinoAposLogin() {
  const voltar = new URLSearchParams(window.location.search).get("voltar") || "";
  return /^[a-z-]+\.html(\?[\w=&%.-]*)?(#[\w-]*)?$/i.test(voltar) ? voltar : "index.html";
}

document.addEventListener("DOMContentLoaded", () => {
  const loginTab = document.querySelector("#login-tab");
  const registerTab = document.querySelector("#register-tab");
  const loginPanel = document.querySelector("#login-panel");
  const registerPanel = document.querySelector("#register-panel");
  const title = document.querySelector("#auth-title");
  const description = document.querySelector("#auth-description");
  const feedback = document.querySelector("#auth-feedback");

  // usuario logado não ve essa tela
  if (estaLogado()) {
    window.location.replace(destinoAposLogin());
    return;
  }

  const motivo = new URLSearchParams(window.location.search).get("motivo");
  if (MENSAGENS_MOTIVO[motivo]) {
    const aviso = document.querySelector("#auth-aviso");
    aviso.textContent = MENSAGENS_MOTIVO[motivo];
    aviso.hidden = false;
  }

  function showFeedback(message, type = "") {
    feedback.textContent = message;
    feedback.className = "auth-feedback";
    if (type) feedback.classList.add(type);
    feedback.hidden = false;
  }

  function clearFeedback() {
    feedback.textContent = "";
    feedback.className = "auth-feedback";
    feedback.hidden = true;
  }

  function switchMode(mode) {
    const isLogin = mode === "login";

    loginPanel.hidden = !isLogin;
    registerPanel.hidden = isLogin;

    loginTab.classList.toggle("active", isLogin);
    registerTab.classList.toggle("active", !isLogin);
    loginTab.setAttribute("aria-selected", String(isLogin));
    registerTab.setAttribute("aria-selected", String(!isLogin));

    title.textContent = isLogin ? "Bem-vindo de volta." : "Crie sua conta.";
    description.textContent = isLogin
      ? "Entre na sua conta e continue acompanhando suas ofertas."
      : "Crie seu perfil e comece a acompanhar seus jogos favoritos.";
    document.title = isLogin ? "Entrar | SavePoint" : "Criar conta | SavePoint";

    clearFeedback();
  }

  loginTab.addEventListener("click", () => switchMode("login"));
  registerTab.addEventListener("click", () => switchMode("register"));
  document.querySelectorAll("[data-switch]").forEach((button) => {
    button.addEventListener("click", () => switchMode(button.dataset.switch));
  });

  function setLoading(form, isLoading) {
    form.querySelector(".auth-submit").disabled = isLoading;
    form.setAttribute("aria-busy", String(isLoading));
  }

  async function entrarERedirecionar(login, senha) {
    const resposta = await SavePointApi.login(login, senha);
    salvarSessao(resposta); // { token, refreshToken }
    window.location.replace(destinoAposLogin());
  }

// login
  const campoIdentificador = document.querySelector("#login-identificador");
  const rotuloIdentificador = document.querySelector("#rotulo-identificador");

  document.querySelectorAll('[name="modo-login"]').forEach((opcao) => {
    opcao.addEventListener("change", () => {
      const porEmail = opcao.value === "email";
      rotuloIdentificador.textContent = porEmail ? "E-mail" : "Nickname";
      campoIdentificador.type = porEmail ? "email" : "text";
      campoIdentificador.placeholder = porEmail ? "voce@exemplo.com" : "Seu nickname";
      campoIdentificador.autocomplete = porEmail ? "email" : "username";
      campoIdentificador.value = "";
      campoIdentificador.focus();
      clearFeedback();
    });
  });

  loginPanel.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearFeedback();
    if (!loginPanel.reportValidity()) return;

    const login = campoIdentificador.value.trim();
    const senha = document.querySelector("#login-password").value;

    setLoading(loginPanel, true);
    showFeedback("Entrando...");
    try {
      await entrarERedirecionar(login, senha);
    } catch (erro) {
      showFeedback(erro.message, "error");
      setLoading(loginPanel, false);
    }
  });

// cadastro
  const campoNick = document.querySelector("#register-username");
  const statusNick = document.querySelector("#nick-status");
  const sugestoesNick = document.querySelector("#nick-sugestoes");
  let nickDisponivel = false;
  let ultimaConsulta = 0;
  let atrasoConsulta;

  function mostrarStatusNick(texto, tipo) {
    statusNick.textContent = texto;
    statusNick.className = `nick-status ${tipo || ""}`;
  }

  function mostrarSugestoes(lista) {
    if (!lista || !lista.length) {
      sugestoesNick.hidden = true;
      sugestoesNick.innerHTML = "";
      return;
    }
    sugestoesNick.innerHTML =
      `<span>Que tal:</span>` +
      lista
        .map((nome) => `<button type="button" class="sugestao" data-nome="${nome.replace(/"/g, "&quot;")}"></button>`)
        .join("");
    // textContent evita que um nickname vire HTML
    sugestoesNick.querySelectorAll(".sugestao").forEach((b) => (b.textContent = b.dataset.nome));
    sugestoesNick.hidden = false;
  }

  async function verificarNick() {
    const nome = campoNick.value.trim();
    nickDisponivel = false;
    mostrarSugestoes([]);
    campoNick.setCustomValidity("");

    if (!nome) {
      mostrarStatusNick("", "");
      return;
    }
    if (!NICKNAME_VALIDO.test(nome)) {
      mostrarStatusNick("Use de 3 a 30 caracteres: letras, números, ponto, hífen ou underline.", "erro");
      campoNick.setCustomValidity("Nickname inválido.");
      return;
    }

    const consulta = ++ultimaConsulta;
    mostrarStatusNick("Verificando...", "");
    try {
      const resposta = await SavePointApi.nomeDisponivel(nome);
      if (consulta !== ultimaConsulta) return; // chegou uma resposta antiga: ignora
      if (resposta.disponivel) {
        nickDisponivel = true;
        mostrarStatusNick("✓ Nickname disponível", "ok");
      } else {
        campoNick.setCustomValidity("Este nickname já está em uso.");
        mostrarStatusNick("✕ Este nickname já está em uso.", "erro");
        mostrarSugestoes(resposta.sugestoes);
      }
    } catch {
      if (consulta !== ultimaConsulta) return;
      // sem resposta do servidor: deixa tentar o cadastro
      mostrarStatusNick("Não foi possível verificar agora.", "");
      nickDisponivel = true;
    }
  }

  campoNick.addEventListener("input", () => {
    clearTimeout(atrasoConsulta);
    atrasoConsulta = setTimeout(verificarNick, 400);
  });

  sugestoesNick.addEventListener("click", (evento) => {
    const botao = evento.target.closest(".sugestao");
    if (!botao) return;
    campoNick.value = botao.dataset.nome;
    campoNick.focus();
    verificarNick();
  });

  registerPanel.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearFeedback();

    // garante que checagem terminou antes de enviar
    clearTimeout(atrasoConsulta);
    await verificarNick();
    if (!registerPanel.reportValidity()) return;
    if (!nickDisponivel) {
      showFeedback("Escolha um nickname disponível.", "error");
      campoNick.focus();
      return;
    }

    const nome = campoNick.value.trim();
    const email = document.querySelector("#register-email").value.trim();
    const password = document.querySelector("#register-password").value;
    const confirmPassword = document.querySelector("#register-confirm-password").value;

    if (password !== confirmPassword) {
      showFeedback("As senhas não coincidem. Confira os dois campos.", "error");
      document.querySelector("#register-confirm-password").focus();
      return;
    }

    setLoading(registerPanel, true);
    showFeedback("Criando sua conta...");
    try {
      await SavePointApi.cadastrar(nome, email, password);
      await entrarERedirecionar(email, password);
    } catch (erro) {
      showFeedback(erro.message, "error");
      if (/nickname/i.test(erro.message)) verificarNick();
      setLoading(registerPanel, false);
    }
  });

  // se veio de "Criar conta", já abre a aba cadastro
  switchMode(new URLSearchParams(window.location.search).get("aba") === "cadastro" ? "register" : "login");
});
