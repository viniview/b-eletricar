
const btn = document.getElementById("accessibility-btn");
const panel = document.getElementById("accessibility-panel");
const closeBtn = document.getElementById("accessibility-close");

const html = document.documentElement;
const body = document.body;

// Recuperar configurações salvas
let fontSize = Number(localStorage.getItem("beletricar-font-size")) || 100;

let highContrast =
    localStorage.getItem("beletricar-high-contrast") === "true";

// Aplicar configurações
function applyPreferences() {
    html.style.fontSize = fontSize + "%";

    body.classList.toggle("high-contrast", highContrast);

    const contrastButton = document.getElementById("contrast-toggle");

    contrastButton?.setAttribute(
        "aria-pressed",
        String(highContrast)
    );

    if (contrastButton) {
        contrastButton.textContent = highContrast
            ? "Desativar alto contraste"
            : "Alto contraste";
    }
}

// Aplicar ao entrar em qualquer página
applyPreferences();

// Abrir painel
function openAccessibility() {
    if (!panel) return;

    panel.hidden = false;
    btn?.setAttribute("aria-expanded", "true");
    closeBtn?.focus();
}

// Fechar painel
function closeAccessibility() {
    if (!panel) return;

    panel.hidden = true;
    btn?.setAttribute("aria-expanded", "false");
    btn?.focus();
}

// Botão principal
btn?.addEventListener("click", () => {
    if (panel.hidden) {
        openAccessibility();
    } else {
        closeAccessibility();
    }
});

// Botão de fechar
closeBtn?.addEventListener("click", closeAccessibility);

// Fechar com ESC
document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && panel && !panel.hidden) {
        closeAccessibility();
    }
});

// Aumentar fonte
document.getElementById("font-increase")?.addEventListener("click", () => {
    fontSize = Math.min(fontSize + 12.5, 150);

    localStorage.setItem("beletricar-font-size", String(fontSize));

    applyPreferences();
});

// Diminuir fonte
document.getElementById("font-decrease")?.addEventListener("click", () => {
    fontSize = Math.max(fontSize - 12.5, 75);

    localStorage.setItem("beletricar-font-size", String(fontSize));

    applyPreferences();
});

// Ativar ou desativar alto contraste
document.getElementById("contrast-toggle")?.addEventListener("click", () => {
    highContrast = !highContrast;

    localStorage.setItem(
        "beletricar-high-contrast",
        String(highContrast)
    );

    applyPreferences();
});

// Restaurar configurações
document.getElementById("font-reset")?.addEventListener("click", () => {
    fontSize = 100;
    highContrast = false;

    localStorage.setItem("beletricar-font-size", "100");
    localStorage.setItem("beletricar-high-contrast", "false");

    applyPreferences();
});
