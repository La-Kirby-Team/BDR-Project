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
    Promise.all([fetchMagasins(), fetchVendeur(vendeurId)]).catch(() => {});

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

    let magasinsLoaded = false; // Variable pour suivre si les magasins ont été chargés

    function fetchMagasins(forceReload = false) {
        if (magasinsLoaded && !forceReload) {
            return Promise.resolve(); // Ignorer l'appel si les magasins ont déjà été chargés
        }
        magasinsLoaded = true; // Marque les magasins comme chargés

        const magasinSelect = document.getElementById('idMagasin');
        magasinSelect.innerHTML = ''; // Efface toutes les options existantes

        return fetch('/api/magasins')
            .then(response => response.json())
            .then(data => {
                const addedIds = new Set(); // Utilisé pour éviter les doublons
                data.forEach(magasin => {
                    if (!addedIds.has(magasin.id)) {
                        const option = document.createElement('option');
                        option.value = magasin.id;
                        option.textContent = magasin.nom;
                        magasinSelect.appendChild(option);
                        addedIds.add(magasin.id); // Marque l'ID comme ajouté
                    }
                });
            });
    }

    function fetchVendeur(id) {
        return fetch(`/api/vendeur/${id}`)
            .then(response => response.json())
            .then(vendeur => {
                initialData = vendeur;
                document.getElementById("username").value = vendeur.nom;
                document.getElementById("salaire").value = vendeur.salaire;
                document.getElementById("estActif").value = vendeur.estActif.toString();
                document.getElementById("idMagasin").value = vendeur.idMagasin;

                // Mise à jour de l'avatar
                const avatarPath = `src/main/resources/public/avatars/${vendeur.nom.toLowerCase().replace(' ', '_')}.png?${new Date().getTime()}`;
                document.getElementById("avatar").src = avatarPath;
            });
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
        const updatedData = {
            nom: document.getElementById("username").value,
            salaire: parseFloat(document.getElementById("salaire").value),
            estActif: document.getElementById("estActif").value === "true",
            idMagasin: parseInt(document.getElementById("idMagasin").value),
        };

        const file = avatarUpload.files[0];
        const imagePromise = file ? uploadAvatar(file, updatedData.nom) : Promise.resolve();
        const profilePromise = updateProfile(updatedData);

        Promise.all([imagePromise, profilePromise])
            .then(() => {
                toggleEditMode(false);

                // Rafraîchir la page pour afficher les nouvelles modifications
                window.location.href = window.location.href.split('?')[0] + '?reload=' + new Date().getTime();
            })
            .catch(() => {
                alert("Une erreur est survenue lors de la mise à jour du profil ou de l'image.");
            });
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
                if (!response.ok) {
                    throw new Error("Erreur lors de l'upload de l'image.");
                }
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
