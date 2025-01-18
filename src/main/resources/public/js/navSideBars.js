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

document.addEventListener('DOMContentLoaded', function () {
    // Bouton de déconnexion
    const logoutButton = document.getElementById('logout-btn');
    if (logoutButton) {
        logoutButton.addEventListener('click', async function () {
            try {
                // Envoyer une requête POST au backend pour la déconnexion
                const response = await fetch('/api/logout', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });

                if (response.ok) {
                    alert("Déconnexion réussie !");
                    window.location.href = '/index.html';  // Rediriger vers la page d'accueil après déconnexion
                } else {
                    alert("Erreur lors de la déconnexion. Veuillez réessayer.");
                }
            } catch (error) {
                console.error("Erreur lors de la déconnexion : ", error);
                alert("Une erreur est survenue. Veuillez réessayer.");
            }
        });
    }

    // Bouton de profil
    const profileButton = document.getElementById('profile-btn');
    if (profileButton) {
        profileButton.addEventListener('click', async function () {
            try {
                // Envoyer une requête GET pour récupérer les informations du profil
                const response = await fetch('/api/profile', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });

                if (response.ok) {
                    const userProfile = await response.json();

                    // Optionnel : Vous pouvez stocker ces informations dans le stockage local/sessionStorage
                    // localStorage.setItem("userProfile", JSON.stringify(userProfile));

                    console.log("Profil de l'utilisateur : ", userProfile);

                    // Rediriger vers la page profil.html après la récupération du profil
                    window.location.href = '/profil.html';  // Redirection vers le profil
                } else {
                    alert("Erreur lors de la récupération du profil. Veuillez réessayer.");
                }
            } catch (error) {
                console.error("Erreur lors de la récupération du profil : ", error);
                alert("Une erreur est survenue. Veuillez réessayer.");
            }
        });
    }
});

