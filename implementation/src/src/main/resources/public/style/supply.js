// Sélection des éléments
const menuIcon = document.getElementById('menu-icon');
const menu = document.getElementById('choixMagasin');

// Ajout d'un événement au clic
menuIcon.addEventListener('click', () => {
    // Basculer la classe 'active' pour afficher/masquer le menu
    menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
});
