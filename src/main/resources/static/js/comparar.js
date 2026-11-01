(() => {
    const form = document.querySelector("#comparison-form");
    const picker = document.querySelector("#picker");
    if (!form || !picker) return;

    const search = document.querySelector("#picker-busca");
    const brandSelect = document.querySelector("#picker-marca");
    const modelSelect = document.querySelector("#picker-modelo");
    const bodySelect = document.querySelector("#picker-carroceria");
    const results = document.querySelector("#picker-resultados");
    const status = document.querySelector("#picker-status");
    const submit = document.querySelector("#comparison-submit");
    const hint = document.querySelector("#comparison-hint");
    const apiUrl = form.dataset.apiUrl;
    let activeSlot = null;
    let requestController = null;
    let knownCars = [];
    let selectedCount = 0;

    function option(label, value = "") {
        const element = document.createElement("option");
        element.value = value;
        element.textContent = label;
        return element;
    }

    function updateComparisonButton() {
        selectedCount = [...form.querySelectorAll('input[type="hidden"][name^="carro"]')]
            .filter((input) => input.value).length;
        submit.disabled = selectedCount < 2;
        hint.textContent = selectedCount < 2
            ? "Selecione ao menos 2 carros para comparar (máximo de 3)."
            : `${selectedCount} veículo(s) selecionado(s).`;
    }

    function fillFilters(cars) {
        const selectedBrand = brandSelect.value;
        const brands = [...new Set(cars.map((car) => car.marca))].sort((a, b) => a.localeCompare(b));
        brandSelect.replaceChildren(option("Todas as marcas"));
        brands.forEach((brand) => brandSelect.append(option(brand, brand)));
        brandSelect.value = brands.includes(selectedBrand) ? selectedBrand : "";

        const models = [...new Set(cars
            .filter((car) => !brandSelect.value || car.marca === brandSelect.value)
            .map((car) => car.modelo))].sort((a, b) => a.localeCompare(b));
        const selectedModel = modelSelect.value;
        modelSelect.replaceChildren(option("Todos os modelos"));
        models.forEach((model) => modelSelect.append(option(model, model)));
        modelSelect.disabled = !brandSelect.value;
        modelSelect.value = models.includes(selectedModel) ? selectedModel : "";

        const bodies = [...new Set(cars.map((car) => car.carroceria).filter(Boolean))]
            .sort((a, b) => a.localeCompare(b));
        const selectedBody = bodySelect.value;
        bodySelect.replaceChildren(option("Todas"));
        bodies.forEach((body) => bodySelect.append(option(body, body)));
        bodySelect.value = bodies.includes(selectedBody) ? selectedBody : "";
    }

    function renderCars(cars) {
        results.replaceChildren();
        const selectedIds = new Set([...form.querySelectorAll('input[type="hidden"][name^="carro"]')]
            .filter((input) => input.closest("[data-slot]").dataset.slot !== activeSlot && input.value)
            .map((input) => input.value));
        cars = cars.filter((car) => !selectedIds.has(String(car.id)));
        if (cars.length === 0) {
            status.textContent = "Nenhum veículo encontrado com esses filtros.";
            return;
        }
        status.textContent = `${cars.length} veículo(s) encontrado(s).`;
        cars.forEach((car) => {
            const item = document.createElement("li");
            const button = document.createElement("button");
            const name = document.createElement("span");
            const meta = document.createElement("span");
            const price = document.createElement("span");
            button.type = "button";
            button.className = "picker__option";
            name.className = "picker__option-name";
            name.textContent = `${car.marca} ${car.modelo}`;
            meta.className = "picker__option-meta";
            meta.textContent = car.carroceria || "Carroceria não informada";
            price.className = "picker__option-price";
            price.textContent = car.preco == null
                ? "Preço não informado"
                : new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" })
                    .format(car.preco);
            button.append(name, meta, price);
            button.addEventListener("click", () => selectCar(car));
            item.append(button);
            results.append(item);
        });
    }

    async function loadCars() {
        if (requestController) requestController.abort();
        requestController = new AbortController();
        const params = new URLSearchParams();
        if (brandSelect.value) params.set("marca", brandSelect.value);
        if (modelSelect.value) params.set("modelo", modelSelect.value);
        if (bodySelect.value) params.set("carroceria", bodySelect.value);
        if (search.value.trim()) params.set("busca", search.value.trim());

        status.textContent = "Buscando veículos no catálogo...";
        results.replaceChildren();
        try {
            const response = await fetch(`${apiUrl}?${params}`, {
                headers: { Accept: "application/json" },
                signal: requestController.signal
            });
            if (!response.ok) throw new Error("Não foi possível carregar os veículos.");
            const cars = await response.json();
            if (params.size === 0) {
                knownCars = cars;
                fillFilters(knownCars);
            }
            renderCars(cars);
        } catch (error) {
            if (error.name === "AbortError") return;
            status.textContent = error.message;
        }
    }

    function selectCar(car) {
        const slot = form.querySelector(`[data-slot="${activeSlot}"]`);
        if (!slot) return;
        slot.querySelector('input[type="hidden"]').value = car.id;
        slot.querySelector("[data-slot-empty]").hidden = true;
        slot.querySelector("[data-slot-filled]").hidden = false;
        slot.querySelector("[data-slot-marca]").textContent = car.marca;
        slot.querySelector("[data-slot-modelo]").textContent = car.modelo;
        slot.querySelector("[data-slot-categoria]").textContent = car.carroceria || "";
        slot.querySelector("[data-slot-preco]").textContent = car.preco == null
            ? "Preço não informado"
            : new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" })
                .format(car.preco);
        const foto = slot.querySelector("[data-slot-foto]");
        if (foto) {
            foto.src = car.fotoUrl
                ? `/images/carros/${car.fotoUrl}`
                : "/images/carros/default.jpg";
            foto.alt = `${car.marca} ${car.modelo}`;
        }
        slot.classList.add("comparison-card--filled");
        slot.querySelector('[data-action="selecionar"]').textContent = "Trocar veículo";
        slot.querySelector('[data-action="remover"]').hidden = false;
        picker.close();
        updateComparisonButton();
    }

    form.addEventListener("click", (event) => {
        const button = event.target.closest("[data-action]");
        if (!button) return;
        const slot = button.closest("[data-slot]");
        if (!slot) return;
        if (button.dataset.action === "selecionar") {
            activeSlot = slot.dataset.slot;
            search.value = "";
            brandSelect.value = "";
            modelSelect.value = "";
            bodySelect.value = "";
            modelSelect.disabled = true;
            picker.showModal();
            loadCars();
            search.focus();
        } else {
            slot.querySelector('input[type="hidden"]').value = "";
            slot.querySelector("[data-slot-empty]").hidden = false;
            slot.querySelector("[data-slot-filled]").hidden = true;
            slot.classList.remove("comparison-card--filled");
            button.hidden = true;
            slot.querySelector('[data-action="selecionar"]').textContent = "Selecionar veículo";
            updateComparisonButton();
        }
    });

    search.addEventListener("input", loadCars);
    brandSelect.addEventListener("change", () => {
        modelSelect.value = "";
        fillFilters(knownCars);
        loadCars();
    });
    modelSelect.addEventListener("change", loadCars);
    bodySelect.addEventListener("change", loadCars);
    document.querySelector("[data-picker-close]").addEventListener("click", () => picker.close());
    picker.addEventListener("click", (event) => {
        if (event.target === picker) picker.close();
    });
    updateComparisonButton();
})();
