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
        if (magasinsLoaded && !forceReload) {
            console.log("fetchMagasins ignoré, déjà chargé.");
            return Promise.resolve(); // Ignorer l'appel si déjà chargé
        }
        magasinsLoaded = true;

        const magasinSelect = document.getElementById('idMagasin');
        magasinSelect.innerHTML = ''; // Efface toutes les options existantes

        return fetch('/api/magasins')
            .then(response => response.json())
            .then(data => {
                const addedIds = new Set(); // Utilisez un ensemble pour éviter les doublons
                data.forEach(magasin => {
                    if (!addedIds.has(magasin.id)) {
                        const option = document.createElement('option');
                        option.value = magasin.id;
                        option.textContent = magasin.nom;
                        magasinSelect.appendChild(option);
                        addedIds.add(magasin.id);
                    }
                });
            })
            .catch(error => console.error('Erreur lors du chargement des magasins:', error));
    }

    function fetchVendeur(id) {
        return fetch(`/api/vendeur/${id}`)
            .then(response => response.json())
            .then(vendeur => {
                console.log("Données du vendeur :", vendeur); // Ajoutez ce log
                initialData = vendeur;
                document.getElementById("username").value = vendeur.nom;
                document.getElementById("salaire").value = vendeur.salaire;
                document.getElementById("estActif").value = vendeur.estActif.toString();
                document.getElementById("idMagasin").value = vendeur.idMagasin;
                if (vendeur.avatar) {
                    document.getElementById("avatar").src = vendeur.avatar;
                }
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
        if (file) {
            // Vérifiez les formats acceptés
            const allowedFormats = ['image/png', 'image/jpeg', 'image/jpg'];
            if (!allowedFormats.includes(file.type)) {
                alert('Format de fichier non pris en charge. Veuillez sélectionner un fichier JPG, JPEG ou PNG.');
                return;
            }
        }

        const imagePromise = file
            ? uploadAvatar(file, updatedData.nom)
            : Promise.resolve(); // Ne rien faire si aucun fichier n'est uploadé

        const profilePromise = updateProfile(updatedData);

        Promise.all([imagePromise, profilePromise])
            .then(() => {
                alert("Profil et image mis à jour avec succès !");
                toggleEditMode(false);
            })
            .catch((error) => {
                console.error("Erreur lors de la mise à jour :", error);
                alert("Une erreur est survenue lors de la mise à jour du profil ou de l'image.");
            });
    }

    function uploadAvatar(file, nom) {
        const formData = new FormData();
        formData.append("avatar", file);
        formData.append("nom", nom);
        formData.append("prenom", ""); // Remplacez par le prénom si nécessaire

        return fetch(`/api/uploadAvatar?id=1&nom=${nom}`, {
            method: "POST",
            body: formData
        }).then(response => {
            console.log("Réponse de l'upload:", response.status, response.statusText);
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