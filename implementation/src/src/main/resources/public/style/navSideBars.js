document.addEventListener('DOMContentLoaded', function () {
    // Load navbar
    fetch('navBar.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('navbar-placeholder').innerHTML = data;

            // Add dark mode toggle listener after loading the navbar
            const themeToggle = document.getElementById('theme-toggle');
            const body = document.body;

            themeToggle.addEventListener('click', () => {
                body.classList.toggle('light-mode');
                themeToggle.textContent = body.classList.contains('light-mode') ? 'Mode Sombre' : 'Mode Clair';
            });
        });

    // Load sidebar
    fetch('sideBar.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('sidebar-placeholder').innerHTML = data;

            // Add sidebar toggle listener after loading the sidebar
            const sidebar = document.querySelector('.sidebar');
            const overlay = document.querySelector('.overlay');
            const menuToggle = document.getElementById('menu-toggle');

            menuToggle.addEventListener('click', () => {
                sidebar.classList.toggle('open');
                overlay.classList.toggle('show');
            });

            overlay.addEventListener('click', () => {
                sidebar.classList.remove('open');
                overlay.classList.remove('show');
            });
        });
});