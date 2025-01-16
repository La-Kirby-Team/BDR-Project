document.addEventListener("DOMContentLoaded", function() {
    // Remplissage de la liste déroulante des magasins
    fetch('/api/magasins')
        .then(response => response.json())
        .then(data => {
            const magasinSelect = document.getElementById('idMagasin');
            data.forEach(magasin => {
                const option = document.createElement('option');
                option.value = magasin.id;
                option.textContent = magasin.nom;
                magasinSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Erreur:', error));

    // Remplissage des détails du vendeur
    fetch('/api/getVendeur.php?id=1')
        .then(response => response.json())
        .then(data => {
            document.getElementById('idMagasin').value = data.idMagasin;
            document.getElementById('username').value = data.nom;
            document.getElementById('salaire').value = data.salaire;
            document.getElementById('estActif').value = data.estActif.toString();
            if (data.avatar) {
                document.getElementById('avatar').src = data.avatar;
            }
        })
        .catch(error => console.error('Erreur:', error));
});