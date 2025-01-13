document.addEventListener('DOMContentLoaded', function () {
    console.log("📌 Script chargé !");

    const form = document.getElementById('dynamic-form');

    if (!form) {
        console.error("⚠️ Le formulaire #dynamic-form n'a pas été trouvé !");
        return;
    }

    form.addEventListener('submit', async function (event) {
        event.preventDefault(); // Empêche le rechargement de la page
        console.log("🚀 Formulaire soumis !");

        // Vérifier si tous les champs sont remplis
        const inputs = document.querySelectorAll('#dynamic-form input, #dynamic-form select');
        let isValid = true;

        inputs.forEach(input => {
            if (input.value.trim() === '') {
                input.classList.add('is-invalid'); // Bordure rouge Bootstrap
                isValid = false;
            } else {
                input.classList.remove('is-invalid'); // Enlève l'erreur
            }
        });

        if (!isValid) {
            alert("❌ Tous les champs doivent être remplis avant de soumettre !");
            return;
        }

        // Récupérer les valeurs du formulaire
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

            const response = await fetch('http://localhost:8080/api/add-supply', {
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
    });
});
