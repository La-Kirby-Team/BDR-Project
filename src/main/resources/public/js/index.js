document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('login-form');

    loginForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        // Get input values
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        // Ensure JSON format
        const userCredentials = JSON.stringify({
            username: username,
            password: password
        });

        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: userCredentials
            });

            if (response.ok) {
                alert("Connexion réussie !");
                window.location.href = '/html/mainMenu.html';
            } else {
                alert("Échec de la connexion. Vérifiez vos identifiants.");
            }
        } catch (error) {
            console.error("Erreur lors de la soumission du formulaire :", error);
            alert("Une erreur est survenue.");
        }
    });
});
