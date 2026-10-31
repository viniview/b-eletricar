const botaoFiltros = document.getElementById("toggle-filtros");
const sidebarFiltros = document.getElementById("sidebar-filtros");

if (botaoFiltros && sidebarFiltros) {

    // Abrir e fechar a sidebar
    botaoFiltros.addEventListener("click", () => {
        const abrir = sidebarFiltros.hidden;

        sidebarFiltros.hidden = !abrir;

        botaoFiltros.setAttribute(
            "aria-expanded",
            String(abrir)
        );
    });

    // Manter a sidebar aberta caso exista uma mensagem de erro
    if (sidebarFiltros.querySelector(".mensagem-erro")) {
        sidebarFiltros.hidden = false;
        botaoFiltros.setAttribute("aria-expanded", "true");
    }
}