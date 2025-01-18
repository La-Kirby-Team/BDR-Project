document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('provider-form');

    // Vérifier si le formulaire existe
    if (!form) {
        console.error("Le formulaire n'a pas été trouvé.");
        return;
    }

    // Écouteur d'événement pour la soumission du formulaire
    form.addEventListener('submit', async function(event) {
        event.preventDefault(); // Empêche la soumission classique du formulaire

        // Récupérer les valeurs des champs
        const nameProvider = document.getElementById('name').value;
        const addressProvider = document.getElementById('address').value;
        const phoneProvider = document.getElementById('phoneNumber').value;

        // Vérification que tous les champs sont remplis
        if (!nameProvider || !addressProvider || !phoneProvider) {
            alert('Tous les champs doivent être remplis avant de soumettre le formulaire.');
            return; // Si un champ est vide, on arrête la soumission
        }

        // Créer un objet contenant les données à envoyer
        const providerData = {
            name: nameProvider,
            address: addressProvider,
            phone: phoneProvider
        };

        // Afficher les données dans la console pour vérification
        console.log("Données à envoyer : ", providerData);

        try {
            // Envoi des données au backend via une requête POST
            const response = await fetch('/api/providers', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(providerData) // Convertir l'objet en JSON
            });

            if (!response.ok) {
                throw new Error('Erreur lors de l\'envoi des données au serveur');
            }

            // Traiter la réponse du serveur
            const result = await response.json();
            alert(result.message || 'Le fournisseur a été ajouté avec succès.');

            // Réinitialiser le formulaire après soumission
            form.reset();
        } catch (error) {
            console.error("Erreur lors de l'envoi des données : ", error);
            alert('Une erreur est survenue. Veuillez réessayer.');
        }
    });
});
