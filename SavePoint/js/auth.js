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

    loginPanel.addEventListener("submit", (event) => {
        event.preventDefault();
        clearFeedback();

        if (!loginPanel.reportValidity()) {
            return;
        }

        showFeedback(
            "A interface está pronta! O login será habilitado quando conectarmos o sistema de autenticação.",
            "success"
        );
    });

    registerPanel.addEventListener("submit", (event) => {
        event.preventDefault();
        clearFeedback();

        if (!registerPanel.reportValidity()) {
            return;
        }

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

        showFeedback(
            "O formulário está válido! A criação da conta será habilitada quando conectarmos o back-end.",
            "success"
        );
    });

    switchMode("login");
});