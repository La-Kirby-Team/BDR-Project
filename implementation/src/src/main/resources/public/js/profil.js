document.addEventListener("DOMContentLoaded", function () {
    // Variables pour stocker les données initiales
    let initialData = {};
    const vendeurId = 1; // ID du vendeur (remplacez-le dynamiquement si nécessaire)

    // Boutons et champs du formulaire
    const editButton = document.getElementById("edit-button");
    const cancelButton = document.getElementById("cancel-button");
    const form = document.getElementById("profile-form");
    const editButtons = document.getElementById("edit-buttons");
    const avatarContainer = document.getElementById("upload-avatar-container");
    const avatarUpload = document.getElementById("avatar-upload");
    const fields = form.querySelectorAll("input, select");

    // Charger les magasins et le profil du vendeur
    Promise.all([fetchMagasins(), fetchVendeur(vendeurId)])
        .catch(error => console.error("Erreur lors du chargement des données:", error));

    // Gestion du bouton Modifier
    editButton.addEventListener("click", () => {
        toggleEditMode(true);
    });

    // Gestion du bouton Annuler
    cancelButton.addEventListener("click", () => {
        toggleEditMode(false);
        resetFormFields();
    });

    // Gestion du formulaire de sauvegarde
    form.addEventListener("submit", (e) => {
        e.preventDefault();
        saveProfile();
    });

    function fetchMagasins(forceReload = false) {
        const magasinSelect = document.getElementById('idMagasin');
        magasinSelect.innerHTML = ''; // Efface toutes les options existantes

        return fetch('/api/magasins')
            .then(response => response.json())
            .then(data => {
                data.forEach(magasin => {
                    const option = document.createElement('option');
                    option.value = magasin.id;
                    option.textContent = magasin.nom;
                    magasinSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Erreur lors du chargement des magasins:', error));
    }

    function fetchVendeur(id) {
        return fetch(`/api/vendeur/${id}`)
            .then(response => response.json())
            .then(vendeur => {
                console.log("Données du vendeur :", vendeur);
                initialData = vendeur;
                document.getElementById("username").value = vendeur.nom;
                document.getElementById("salaire").value = vendeur.salaire;
                document.getElementById("estActif").value = vendeur.estActif.toString();
                document.getElementById("idMagasin").value = vendeur.idMagasin;

                // Mise à jour de l'avatar
                const avatarPath = `/avatars/${vendeur.nom.toLowerCase().replace(' ', '_')}.png?${new Date().getTime()}`;
                document.getElementById("avatar").src = avatarPath;
            })
            .catch(error => console.error('Erreur lors du chargement du profil du vendeur:', error));
    }

    function toggleEditMode(isEditing) {
        fields.forEach(field => {
            if (isEditing) {
                field.removeAttribute("readonly");
                field.removeAttribute("disabled");
            } else {
                field.setAttribute("readonly", true);
                field.setAttribute("disabled", true);
            }
        });
        editButton.classList.toggle("d-none", isEditing);
        editButtons.classList.toggle("d-none", !isEditing);
        avatarContainer.classList.toggle("d-none", !isEditing);
    }

    function resetFormFields() {
        document.getElementById("username").value = initialData.nom;
        document.getElementById("salaire").value = initialData.salaire;
        document.getElementById("estActif").value = initialData.estActif.toString();
        document.getElementById("idMagasin").value = initialData.idMagasin;
    }

    function saveProfile() {
        const idMagasin = document.getElementById('idMagasin').value;

        if (!idMagasin) {
            alert('Veuillez sélectionner un magasin.');
            return;
        }

        const updatedData = {
            nom: document.getElementById('username').value,
            salaire: parseFloat(document.getElementById('salaire').value),
            estActif: document.getElementById('estActif').value === "true",
            idMagasin: parseInt(idMagasin)
        };

        const file = avatarUpload.files[0];
        const imagePromise = file ? uploadAvatar(file, updatedData.nom) : Promise.resolve();
        const profilePromise = updateProfile(updatedData);

        Promise.all([imagePromise, profilePromise])
            .then(() => {
                alert("Profil et image mis à jour avec succès !");
                toggleEditMode(false);

                // Vérification périodique pour charger la nouvelle image
                const updatedAvatarPath = `/avatars/${updatedData.nom.toLowerCase().replace(' ', '_')}.png?reload=${Math.random()}`;
                checkImageUpdate(updatedAvatarPath, 500, 5000); // Vérifie toutes les 500ms, maximum 5 secondes
            })
            .catch(error => {
                console.error("Erreur lors de la mise à jour :", error);
                alert("Une erreur est survenue lors de la mise à jour du profil ou de l'image.");
            });
    }

    function checkImageUpdate(imagePath, interval, timeout) {
        const startTime = Date.now();

        const check = () => {
            const img = new Image();
            img.onload = () => {
                document.getElementById("avatar").src = imagePath;
                console.log("Nouvelle image chargée :", imagePath);
            };
            img.onerror = () => {
                if (Date.now() - startTime < timeout) {
                    console.log("Nouvelle image non encore disponible, nouvelle tentative...");
                    setTimeout(check, interval); // Réessaie après l'intervalle
                } else {
                    console.error("Nouvelle image non disponible après le délai imparti.");
                    alert("La nouvelle image n'a pas pu être rechargée. Veuillez actualiser la page.");
                }
            };
            img.src = imagePath; // Déclenche le chargement de l'image
        };

        check(); // Lance la vérification initiale
    }


    function uploadAvatar(file, nom) {
        const formData = new FormData();
        formData.append("avatar", file);
        formData.append("nom", nom);

        return fetch(`/api/uploadAvatar?id=1&nom=${nom}`, {
            method: "POST",
            body: formData
        })
            .then(response => {
                console.log("Réponse de l'upload:", response.status, response.statusText);
                if (!response.ok) {
                    throw new Error("Erreur lors de l'upload de l'image.");
                }
            })
            .catch(error => {
                console.error("Erreur d'upload :", error);
                throw error;
            });
    }

    function updateProfile(data) {
        return fetch("/api/updateVendeur/1", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        }).then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la mise à jour du profil.");
            }
        });
    }
});
