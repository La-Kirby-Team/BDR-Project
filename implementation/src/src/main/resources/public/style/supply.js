// Sélection des éléments
const menuIcon = document.getElementById('menu-icon');
const menu = document.getElementById('choixMagasin');

// Ajout d'un événement au clic
menuIcon.addEventListener('click', () => {
    // Basculer la classe 'active' pour afficher/masquer le menu
    menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
});

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('dynamic-form').addEventListener('submit', async function (event) {
        event.preventDefault(); // Empêche le rechargement de la page


        // Vérifier si tous les champs sont remplis
        const inputs = document.querySelectorAll('#dynamic-form input, #dynamic-form select');
        let isValid = true;

        inputs.forEach(input => {
            if (input.value.trim() === '') {
                input.classList.add('is-invalid'); // Ajoute une bordure rouge Bootstrap
                isValid = false;
            } else {
                input.classList.remove('is-invalid'); // Enlève l'erreur si corrigée
            }
        });

        if (!isValid) {
            alert("❌ Tous les champs doivent être remplis avant de soumettre !");
            return; // Bloque l'envoi des données
        }


        // Récupérer les valeurs du formulaire
        const formData = new FormData(this);
        let data = {};

        // Convertir FormData en objet JSON
        formData.forEach((value, key) => {
            if (!data[key]) {
                data[key] = [];
            }
            data[key].push(value);
        });

        console.log("Données envoyées :", data);

        try {
            const response = await fetch('/api/add-supply', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            const result = await response.json();
            alert(result.message);
        } catch (error) {
            console.error("Erreur lors de l'envoi des données :", error);
        }
    });
});

