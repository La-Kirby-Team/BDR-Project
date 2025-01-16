document.addEventListener('DOMContentLoaded', function () {
    console.log("\ud83d\udccc Script chargé !");

    const form = document.getElementById('dynamic-form');

    if (!form) {
        console.error("⚠️ Le formulaire #dynamic-form n'a pas été trouvé !");
        return;
    }

    console.log("✅ Formulaire trouvé :", form);

    console.log("✅ Écouteur d'événement submit attaché !");

    form.addEventListener('submit', async function (event) {
        event.preventDefault();
        console.log("🚀 Formulaire soumis !");

        const inputs = document.querySelectorAll('#dynamic-form input, #dynamic-form select');
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

        try {
            const testAPI = await fetch('/api/add-supply', { method: 'POST' });
            if (!testAPI.ok) {
                throw new Error("API inaccessible");
            }
            console.log("✅ API accessible !");
        } catch (error) {
            console.error("🚨 problème dans l'API !", error);
            alert("🚨 problème dans l'API !");
            return;
        }

        const formData = new FormData(form);
        let data = {};


        formData.forEach((value, key) => {
            if (!data[key]) {
                data[key] = [];
            }
            data[key].push(value);
        });


        console.log("📡 Données envoyées :", JSON.stringify(data, null, 2));

        try {
            console.log("📡 Envoi des données à l'API...");

            const response = await fetch('/api/add-supply', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            console.log("✅ Réponse reçue :", response);

            if (!response.ok) {
                const errorResponse = await response.json();
                throw new Error(`Erreur serveur: ${response.status} - ${errorResponse.error || response.statusText}`);
            }

            const result = await response.json();
            alert(result.message);
        } catch (error) {
            console.error("❌ Erreur lors de l'envoi :", error);
            alert("Une erreur est survenue lors de l'envoi des données. Vérifiez la console.");
        }
        const response = await fetch('/api/add-supply', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        console.log("✅ Réponse du serveur :", await response.json());

    });
});

//Ajout de nouveaux champs d'articles
document.addEventListener('DOMContentLoaded', function() {
    let fieldCount = 1;

    document.getElementById('add-field').addEventListener('click', function() {
        fieldCount++;
        const dynamicFields = document.getElementById('dynamic-fields');

        const newFieldRow = document.createElement('div');
        newFieldRow.innerHTML = `
            <fieldset>
                <div class="bordure">
                 <legend>Produit N°${fieldCount}</legend>
                <div class="row mb-3">
                    <div class="col-md-3">
                        <label for="name-${fieldCount}" class="form-label text-left">Produit</label>
                        <input type="text" id="name-${fieldCount}" name="product[]" class="form-control" placeholder="Produit" required>
                    </div>
                    <div class="col-md-3">
                        <label for="volume-${fieldCount}" class="form-label text-left">Volume (cl)</label>
                        <input type="number" id="volume-${fieldCount}" name="volume[]" class="form-control" placeholder="33" required>
                    </div>
                    <div class="col-md-3">
                        <label for="recipient-${fieldCount}" class="form-label text-left">Récipient</label>
                        <select id="recipient-${fieldCount}" name="recipient[]" class="form-control" required>
                            <option value="Can">Canette</option>
                            <option value="Bottle">Bouteille</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label for="provider-${fieldCount}" class="form-label text-left">Fournisseur</label>
                        <input type="text" id="provider-${fieldCount}" name="provider[]" class="form-control" placeholder="Fournisseur" required>
                    </div>
                </div>


                <div class="row mb-3 justify-content">
                    <div class="col-md-4">
                        <label for="EndOfSales-${fieldCount}" class="form-label text-left">Date Fin de Vente</label>
                        <input type="date" id="EndOfSales-${fieldCount}" name="EndOfSales[]" class="form-control" placeholder="Date de fin de série" required>
                    </div>
                    <div class="col-md-4">
                        <label for="Peremption-${fieldCount}" class="form-label text-left">Date Péremption</label>
                        <input type="date" id="Peremption-${fieldCount}" name="Peremption[]" class="form-control" placeholder="Date Péremption" required>
                    </div>
                    <div class="col-md-3">
                        <label for="alcohol-${fieldCount}" class="form-label text-left">Taux d'alcool (%)</label>
                        <input type="number" id="alcohol-${fieldCount}" name="tauxAlcool[]" class="form-control" placeholder="5.0" step="0.1" required>
                    </div>
                </div>

                <div class="highlight row mb-3 justify-content-center">
                     <div class="col-md-4 ">
                         <label for="quantity-${fieldCount}" class="form-label text-left>">Quantité</label>
                         <input type="number" id="quantity-${fieldCount}" name="quantity[]" class="form-control" placeholder="0" required>
                     </div>
                    <div class="col-md-4 ">
                        <label for="prix-${fieldCount}" class="form-label text-left>">Prix</label>
                        <input type="number" id="prix-${fieldCount}" name="prix[]" class="form-control" placeholder="0.3" required>
                    </div>
                 </div>


             </div>
            </fieldset>
        `;

        dynamicFields.appendChild(newFieldRow);
    });

    document.getElementById('remove-field').addEventListener('click', function() {
        const dynamicFields = document.getElementById('dynamic-fields');
        if (fieldCount > 1) {
            dynamicFields.removeChild(dynamicFields.lastElementChild);
            fieldCount--;
        } else {
            alert("Il doit y avoir au moins un produit.");
        }
    });
});

// Fonction pour obtenir la date du jour au format YYYY-MM-DD
function getTodayDate() {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0'); // Mois avec 2 chiffres
    const dd = String(today.getDate()).padStart(2, '0');      // Jour avec 2 chiffres
    return `${yyyy}-${mm}-${dd}`;
}

function initializeDateField(dateField, autoCheck) {
    if (autoCheck.checked) {
        dateField.value = getTodayDate();
        dateField.setAttribute('readonly', true); // Lecture seule
        dateField.removeAttribute('required');   // Non obligatoire
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const dateField = document.getElementById('custom-date-1');
    const autoCheck = document.getElementById('auto-date-1');

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
document.addEventListener('DOMContentLoaded', function () {
    const dateField = document.getElementById('dateJour');
    const autoCheck = document.getElementById('auto-dateJour');

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
