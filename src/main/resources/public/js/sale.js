document.addEventListener('DOMContentLoaded', function () {
    const dateField = document.getElementById('custom-date');
    const autoCheck = document.getElementById('auto-date');

    // Initialisation : remplir le champ avec la date du jour et le rendre readonly

    initializeDateField(dateField, autoCheck); // Appel initial

    // Gérer les interactions avec la case à cocher
    autoCheck.addEventListener('change', function () {
        if (autoCheck.checked) {
            // Si la case est cochée, remplir automatiquement et verrouiller
            dateField.value = getTodayDate();
            dateField.setAttribute('readonly', true); // Lecture seule
            dateField.removeAttribute('required');   // Non obligatoire
        } else {
            // Si la case est décochée, effacer, rendre modifiable et obligatoire
            dateField.value = ''; // Effacer le champ
            dateField.removeAttribute('readonly');   // Permettre l'édition
            dateField.setAttribute('required', true); // Champ obligatoire
        }
    });
});

//Ajout de nouveaux champs d'articles
document.addEventListener('DOMContentLoaded', function() {
    let fieldCount = 1;

    document.getElementById('add-field').addEventListener('click', function() {
        fieldCount++;
        const dynamicFields = document.getElementById('dynamic-sale-fields');

        const newFieldRow = document.createElement('div');
        newFieldRow.innerHTML = `
            <fieldset>
            <!-- Champs dynamiques -->
            <div id="dynamic-sale-fields">

                <div class="bordure">
                    <legend>Produit N°${fieldCount}</legend>
                    <div class="row mb-3">
                        <div class="col-md-4">
                            <label for="name-${fieldCount}" class="form-label text-left">Produit</label>
                            <input type="text" id="name-${fieldCount}" name="product[]" class="form-control" placeholder="Produit" required>
                        </div>
                        <div class="col-md-4">
                            <label for="volume-${fieldCount}" class="form-label text-left">Volume (cl)</label>
                            <input type="number" id="volume-${fieldCount}" name="volume[]" class="form-control" placeholder="33" step ="0.1" min="0.1"required>
                        </div>
                        <div class="col-md-4">
                            <label for="recipient-${fieldCount}" class="form-label text-left">Récipient</label>
                            <select id="recipient-${fieldCount}" name="recipient[]" class="form-control" required>
                                <option value="canette">Canette</option>
                                <option value="bouteille">Bouteille</option>
                            </select>
                        </div>
                    </div>


                    <div class="row mb-3 justify-content">
                        <div class="col-md-3">
                            <label for="alcohol-${fieldCount}" class="form-label text-left">Taux d'alcool (%)</label>
                            <input type="number" id="alcohol-${fieldCount}" name="tauxAlcool[]" class="form-control" placeholder="5.0" step="0.01" min="0.01" required>
                        </div>
                        <div class="col-md-4 ">
                            <label for="quantity-${fieldCount}" class="form-label text-left>">Quantité</label>
                            <input type="number" id="quantity-${fieldCount}" name="quantity[]" class="form-control" placeholder="0" required min="1">
                        </div>
                        <div class="col-md-4 ">
                            <label for="prix-${fieldCount}" class="form-label text-left>"><b>Prix</b></label>
                            <input type="number" id="prix-${fieldCount}" name="prix[]" class="form-control" placeholder="0.3" min="0.01" required min="0">
                        </div>
                    </div>

                </div>
            </div>
        </fieldset>

        `;

        dynamicFields.appendChild(newFieldRow);
    });

    document.getElementById('remove-field').addEventListener('click', function() {
        const dynamicFields = document.getElementById('dynamic-sale-fields');
        if (fieldCount > 1) {
            dynamicFields.removeChild(dynamicFields.lastElementChild);
            fieldCount--;
        } else {
            alert("Il doit y avoir au moins un produit.");
        }
    });
});

document.addEventListener('DOMContentLoaded', function () {
    console.log("\ud83d\udccc Script chargé !");

    const form = document.getElementById('dynamic-sale-form');

    if (!form) {
        console.error("⚠️ Le formulaire #dynamic-sale-form n'a pas été trouvé !");
        return;
    }

    console.log("✅ Formulaire trouvé :", form);

    console.log("✅ Écouteur d'événement submit attaché !");

    form.addEventListener('submit', async function (event) {
        event.preventDefault();
        console.log("🚀 Formulaire soumis !");

        // Valider les champs
        const inputs = document.querySelectorAll('#dynamic-sale-form input, #dynamic-sale-form select');
        let isValid = true;

        inputs.forEach(input => {
            if (input.value.trim() === '') {
                input.classList.add('is-invalid');
                isValid = false;
            } else {
                input.classList.remove('is-invalid');
            }
        });

        if (!isValid) {
            console.warn("❌ Des champs sont vides, formulaire non soumis !");
            alert("❌ Tous les champs doivent être remplis avant de soumettre !");
            return;
        }

        console.log("✅ Validation passée !");

        // Collecter les données du formulaire et les transformer en JSON
        const formData = new FormData(form);
        let data = {};

        formData.forEach((value, key) => {
            // Supprimer les crochets [] des clés
            const cleanedKey = key.replace('[]', '');

            if (!data[cleanedKey]) {
                data[cleanedKey] = [];
            }

            if (!isNaN(value)) {
                if (cleanedKey === 'quantity' || cleanedKey === 'volume') {
                    value = parseInt(value);
                } else {
                    value = parseFloat(value);
                }
            }

            // Ajoutez la valeur convertie au tableau correspondant
            data[cleanedKey].push(value);
        });


        try {
            const response = await fetch('/api/add-sale', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const errorResponse = await response.json();
                throw new Error(`Erreur serveur: ${response.status} - ${errorResponse.error || response.statusText}`);
            }

            const result = await response.json();
            alert(result.message);
        } catch (error) {
            console.error("❌ Erreur lors de l'envoi :", error);
            alert("Une erreur est survenue lors de l'envoi des données.");
        }
    });
});

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('dynamic-sale-form');
    const totalField = document.getElementById('total');

    // Fonction pour recalculer le total
    function updateTotal() {
        let total = 0;

        // Récupérer tous les champs "prix" et "quantité"
        const prices = document.querySelectorAll('[name="prix[]"]');
        const quantities = document.querySelectorAll('[name="quantity[]"]');

        // Additionner le prix * quantité pour chaque produit
        for (let i = 0; i < prices.length; i++) {
            const price = parseFloat(prices[i].value) || 0;
            const quantity = parseInt(quantities[i].value) || 0;
            total += price * quantity;
        }

        // Afficher le total
        totalField.value = total.toFixed(2);
    }

    form.addEventListener('input', function(event) {
        if (event.target.name === 'prix[]' || event.target.name === 'quantity[]') {
            updateTotal();
        }
    });

    updateTotal();
});
