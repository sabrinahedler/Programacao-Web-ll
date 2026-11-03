document.addEventListener("DOMContentLoaded", () => {
    const loginTab = document.querySelector("#login-tab");
    const registerTab = document.querySelector("#register-tab");

    const loginPanel = document.querySelector("#login-panel");
    const registerPanel = document.querySelector("#register-panel");

    const title = document.querySelector("#auth-title");
    const description = document.querySelector("#auth-description");

    const feedback = document.querySelector("#auth-feedback");

    function showFeedback(message, type = "") {
        feedback.textContent = message;
        feedback.className = "auth-feedback";

        if (type) {
            feedback.classList.add(type);
        }

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

        title.textContent = isLogin
            ? "Bem-vindo de volta."
            : "Crie sua conta.";

        description.textContent = isLogin
            ? "Entre na sua conta e continue acompanhando suas ofertas."
            : "Crie seu perfil e comece a acompanhar seus jogos favoritos.";

        document.title = isLogin
            ? "Entrar | SavePoint"
            : "Criar conta | SavePoint";

        clearFeedback();
    }

    loginTab.addEventListener("click", () => {
        switchMode("login");
    });

    registerTab.addEventListener("click", () => {
        switchMode("register");
    });

    document.querySelectorAll("[data-switch]").forEach((button) => {
        button.addEventListener("click", () => {
            switchMode(button.dataset.switch);
        });
    });

    function setLoading(form, isLoading) {
        const button = form.querySelector(".auth-submit");

        button.disabled = isLoading;
        form.setAttribute("aria-busy", String(isLoading));
    }

async function entrarERedirecionar(email, senha) {
    try {
        const resposta = await SavePointApi.login(email, senha);
        const token = (resposta && resposta.token) ? resposta.token : resposta;

        if (token) {
            salvarToken(token);
            window.location.replace("index.html");
        } else {
            alert("Erro ao obter o token de acesso.");
        }
    } catch (erro) {
        alert(erro.message || "E-mail ou senha incorretos.");
    }
}

    loginPanel.addEventListener("submit", async (event) => {
        event.preventDefault();
        clearFeedback();

        if (!loginPanel.reportValidity()) {
            return;
        }

        const email = document.querySelector("#login-email").value.trim();
        const senha = document.querySelector("#login-password").value;

        setLoading(loginPanel, true);
        showFeedback("Entrando...");

        try {
            await entrarERedirecionar(email, senha);
        } catch (erro) {
            showFeedback(erro.message, "error");
        } finally {
            setLoading(loginPanel, false);
        }
    });

    registerPanel.addEventListener("submit", async (event) => {
        event.preventDefault();
        clearFeedback();

        if (!registerPanel.reportValidity()) {
            return;
        }

        const nome = document.querySelector("#register-username").value.trim();
        const email = document.querySelector("#register-email").value.trim();
        const password = document.querySelector("#register-password").value;

        const confirmPassword = document.querySelector(
            "#register-confirm-password"
        ).value;

        if (password !== confirmPassword) {
            showFeedback(
                "As senhas não coincidem. Confira os dois campos.",
                "error"
            );

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
        } finally {
            setLoading(registerPanel, false);
        }
    });

    switchMode("login");
});