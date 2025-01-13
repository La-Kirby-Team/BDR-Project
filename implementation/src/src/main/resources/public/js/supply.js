// Sélection des éléments
const menuIcon = document.getElementById('menu-icon');
const menu = document.getElementById('choixMagasin');

document.addEventListener('DOMContentLoaded', function () {
    let fieldCount = 1; // Compteur pour les champs

    // Événement pour ajouter un champ dynamique
    document.getElementById('add-field').addEventListener('click', function () {
        fieldCount++; // Incrémenter le compteur pour identifier chaque champ
        const dynamicFields = document.getElementById('dynamic-fields');

        // Création de la nouvelle ligne de champs
        const newFieldRow = document.createElement('div');
        newFieldRow.innerHTML = `

            <fieldset>

            <!-- Première ligne de champs -->
            <div class="bordure">
            <legend>Produit N°${fieldCount}</legend>
            <div class="row mb-3">
                <div class="col-md-3">
                    <label for="name-${fieldCount}" class="form-label text-left">Produit</label>
                    <input type="text" id="name-${fieldCount}" name="product[]" class="form-control" placeholder="Produit"required>
                </div>
                <div class="col-md-3">
                    <label for="volume-${fieldCount}" class="form-label text-left">Volume</label>
                    <input type="number" id="volume-${fieldCount}" name="volume[]" class="form-control" placeholder="1 litre" required>
                </div>
                <div class="col-md-3">
                    <label for="recipient-${fieldCount}" class="form-label text-left">Récipient</label>
                    <select id="recipient-${fieldCount}" name="recipient[]" class="form-control" required>
                        <option value="Can">Cannette</option>
                        <option value="Bottle">Bouteille</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label for="provider-${fieldCount}" class="form-label text-left">Fournisseur</label>
                    <input type="text" id="provider-${fieldCount}" name="provider[]" class="form-control" placeholder="Fournisseur" required>
                </div>
            </div>

            <!-- Deuxième ligne de champs -->
            <div class="row mb-3">
                <div class="col-md-4">
                    <label for="EndOfSales-${fieldCount}" class="form-label text-left">Date Fin de Vente</label>
                    <input type="date" id="EndOfSales-${fieldCount}" name="EndOfSales[]" class="form-control" placeholder="Date de fin de série" required>
                </div>
                <div class="col-md-4">
                    <label for="Peremption-${fieldCount}" class="form-label text-left">Date Péremption</label>
                    <input type="date" id="Peremption-${fieldCount}" name="Peremption[]" class="form-control" placeholder="Date péremption" required>
                </div>
            </div>

                <div class="highlight row mb-3 justify-content-center">
                     <div class="col-md-4">
                         <label for="quantity-${fieldCount}" class="form-label text-left>">Quantité</label>
                         <input type="number" id="quantity-${fieldCount}" name="quantity[]" class="form-control" placeholder="0" required>
                     </div>
                    <div class="col-md-4">
                        <label for="prix-${fieldCount}" class="form-label text-left>">Prix</label>
                        <input type="number" id="prix-${fieldCount}" name="prix[]" class="form-control" placeholder="0.0" required>
                    </div>
                </div>
            </div>
            </fieldset>
        `;

        // Ajout de la nouvelle ligne de champs au formulaire
        dynamicFields.appendChild(newFieldRow);
    });

    // Événement pour retirer le dernier champ dynamique
    document.getElementById('remove-field').addEventListener('click', function () {
        const dynamicFields = document.getElementById('dynamic-fields');
        if (fieldCount > 0) { // Ne permet de supprimer que si le nombre de champs est supérieur à 1
            dynamicFields.removeChild(dynamicFields.lastElementChild);
            fieldCount--; // Décrémente le compteur
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
