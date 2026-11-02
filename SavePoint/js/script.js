const searchForm = document.querySelector("#search-form");
const searchInput = document.querySelector("#game-search");
const gameCards = [...document.querySelectorAll(".game-card")];
const filterButtons = [...document.querySelectorAll("[data-filter]")];
const focusSearchButton = document.querySelector("#focus-search");
const menuToggle = document.querySelector(".menu-toggle");
const mainNav = document.querySelector("#main-nav");
const aiForm = document.querySelector("#ai-form");
const aiQuestion = document.querySelector("#ai-question");
const aiFeedback = document.querySelector("#ai-feedback");
const wishlistButton = document.querySelector("#wishlist-button");

function filterGames(query = "", filter = "all") {
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
    document.querySelector("#deal-grid").after(message);
  }
  message.textContent = normalizedQuery
    ? (visibleCount ? `${visibleCount} jogo(s) encontrado(s) nesta demonstração.` : "Nenhum jogo encontrado nesta demonstração.")
    : "";
}

searchForm.addEventListener("submit", (event) => {
  event.preventDefault();
  filterGames(searchInput.value, document.querySelector(".filter-chip.selected")?.dataset.filter || "all");
  document.querySelector("#ofertas").scrollIntoView({ behavior: "smooth" });
});

filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    filterButtons.forEach((item) => item.classList.remove("selected"));
    button.classList.add("selected");
    filterGames(searchInput.value, button.dataset.filter);
  });
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
