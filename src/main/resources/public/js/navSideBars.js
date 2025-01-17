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
