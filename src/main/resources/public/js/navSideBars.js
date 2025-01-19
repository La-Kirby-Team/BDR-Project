document.addEventListener('DOMContentLoaded', function () {
    // Load navbar
    fetch('navBar.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('navbar-placeholder').innerHTML = data;

            // Delay execution to allow DOM to update
            setTimeout(() => {
                const themeToggle = document.getElementById('theme-toggle');
                if (themeToggle) {
                    const body = document.body;
                    themeToggle.addEventListener('click', () => {
                        body.classList.toggle('light-mode');
                        themeToggle.textContent = body.classList.contains('light-mode') ? 'Mode Sombre' : 'Mode Clair';
                    });
                } else {
                    console.warn("⚠️ theme-toggle not found, skipping event listener.");
                }
            }, 100); // Wait 100ms before executing
        })
        .catch(error => console.error("Error loading navbar:", error));

    // Load sidebar
    fetch('sideBar.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('sidebar-placeholder').innerHTML = data;

            // Delay execution to allow DOM to update
            setTimeout(() => {
                const sidebar = document.querySelector('.sidebar');
                const overlay = document.querySelector('.overlay');
                const menuToggle = document.getElementById('menu-toggle');

                if (menuToggle && sidebar && overlay) {
                    menuToggle.addEventListener('click', () => {
                        sidebar.classList.toggle('open');
                        overlay.classList.toggle('show');
                    });

                    overlay.addEventListener('click', () => {
                        sidebar.classList.remove('open');
                        overlay.classList.remove('show');
                    });
                } else {
                    console.warn("⚠️ Sidebar elements not found even after delay. Check sideBar.html structure.");
                }
            }, 100); // Wait 100ms before executing
        })
        .catch(error => console.error("Error loading sidebar:", error));
});

document.addEventListener("DOMContentLoaded", function () {
    setTimeout(() => {
        const dropdownToggle = document.querySelector("#userDropdown"); // Target the dropdown button
        if (dropdownToggle) {
            new bootstrap.Dropdown(dropdownToggle);
        } else {
            console.warn("⚠️ User dropdown not found!");
        }
    }, 300);  // Delay to ensure the navbar is fully loaded
});


document.addEventListener("DOMContentLoaded", function () {
    setTimeout(() => {
        const logoutButton = document.getElementById("logout-button");

        if (!logoutButton) {
            console.warn("⚠️ Bouton de déconnexion introuvable !");
            return;
        }

        logoutButton.addEventListener("click", async function (event) {
            event.preventDefault(); // Empêche la navigation normale
            alert("Déconnexion en cours...");

            // Ajoute un délai avant la requête de déconnexion
            setTimeout(async () => {
                try {
                    const response = await fetch("/api/logout", {
                        method: "POST",
                        credentials: "include", // Assure que les cookies de session sont inclus
                    });

                    if (response.ok) {
                        // Supprime le cookie côté client
                        document.cookie = "session_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

                        // Attendre 500ms avant de rediriger (effet plus fluide)
                        setTimeout(() => {
                            window.location.href = "/html/index.html";
                        }, 500);
                    } else {
                        alert("Erreur lors de la déconnexion. Veuillez réessayer.");
                    }
                } catch (error) {
                    console.error("❌ Erreur lors de la requête de déconnexion :", error);
                    alert("Une erreur est survenue. Veuillez vérifier votre connexion.");
                }
            }, 500); // Attente de 500ms avant l'envoi de la requête
        });
    }, 300); // Attente de 300ms pour que le DOM soit prêt
});
