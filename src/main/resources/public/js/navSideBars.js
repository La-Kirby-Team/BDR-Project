document.addEventListener('DOMContentLoaded', function () {
    // Load navbar
    fetch('navBar.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('navbar-placeholder').innerHTML = data;

            // Delay execution to allow DOM to update
            setTimeout(() => {
                const themeToggle = document.getElementById('theme-toggle');
                const body = document.body;

                // 🟢 Load stored theme preference from localStorage
                const storedTheme = localStorage.getItem('theme');
                if (storedTheme === 'light') {
                    body.classList.add('light-mode');
                    themeToggle.textContent = 'Mode Sombre';
                } else {
                    body.classList.remove('light-mode');
                    themeToggle.textContent = 'Mode Clair';
                }

                if (themeToggle) {
                    themeToggle.addEventListener('click', () => {
                        body.classList.toggle('light-mode');
                        const isLightMode = body.classList.contains('light-mode');

                        // 💾 Save theme preference in localStorage
                        localStorage.setItem('theme', isLightMode ? 'light' : 'dark');

                        // 🏷️ Update button text
                        themeToggle.textContent = isLightMode ? 'Mode Sombre' : 'Mode Clair';
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

// Dropdown for user settings
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

// Logout functionality with timeout
document.addEventListener("DOMContentLoaded", function () {
    setTimeout(() => {
        const logoutButton = document.getElementById("logout-button");

        if (!logoutButton) {
            console.warn("⚠️ Bouton de déconnexion introuvable !");
            return;
        }

        logoutButton.addEventListener("click", async function (event) {
            event.preventDefault(); // Prevent normal navigation

            // ⏳ Delay before logout request
            setTimeout(async () => {
                try {
                    const response = await fetch("/api/logout", {
                        method: "POST",
                        credentials: "include", // Ensure session cookies are included
                    });
                    if (response.ok) {
                        // 🗑️ Remove the session cookie
                        document.cookie = "session_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

                        // ⏳ Wait 500ms before redirecting (smooth transition)
                        setTimeout(() => {
                            window.location.href = "/html/index.html";
                        }, 50);
                    } else {
                        alert("Erreur lors de la déconnexion. Veuillez réessayer.");
                    }
                } catch (error) {
                    console.error("❌ Erreur lors de la requête de déconnexion :", error);
                    alert("Une erreur est survenue. Veuillez vérifier votre connexion.");
                }
            }, 50); // ⏳ Delay of 500ms before sending the logout request
        });
    }, 300); // ⏳ Delay of 300ms to ensure DOM is ready
});

