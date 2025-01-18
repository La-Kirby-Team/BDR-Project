document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('login-form');

    loginForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        // Récupérer les données du formulaire
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        // Préparer les données à envoyer
        const userCredentials = {
            email: email,
            password: password
        };

        try {
            // Envoyer une requête POST au backend
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userCredentials)
            });

            // Vérifier si la réponse est correcte
            if (response.ok) {
                alert("Connexion réussie");
                window.location.href = '/html/mainMenu.html';  // Rediriger vers le menu principal après une connexion réussie
            } else {
                alert("Erreur lors de la connexion. Veuillez vérifier vos informations.");
            }
        } catch (error) {
            console.error("Erreur lors de la soumission du formulaire : ", error);
            alert("Une erreur est survenue. Veuillez réessayer.");
        }
    });
});
