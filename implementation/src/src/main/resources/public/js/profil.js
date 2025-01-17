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

    // Fonctions utilitaires
    function fetchMagasins() {
        return fetch("/api/magasins")
            .then(response => response.json())
            .then(data => {
                const magasinSelect = document.getElementById("idMagasin");
                data.forEach(magasin => {
                    const option = document.createElement("option");
                    option.value = magasin.id;
                    option.textContent = magasin.nom;
                    magasinSelect.appendChild(option);
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
        const updatedData = {
            nom: document.getElementById("username").value,
            salaire: parseFloat(document.getElementById("salaire").value),
            estActif: document.getElementById("estActif").value === "true",
            idMagasin: parseInt(document.getElementById("idMagasin").value),
            ancienNom: initialData.nom,
            ancienPrenom: "" // Ajoutez le prénom si nécessaire
        };

        const file = avatarUpload.files[0];
        if (file) {
            const formData = new FormData();
            formData.append("avatar", file);
            formData.append("nom", updatedData.nom);
            formData.append("prenom", ""); // Ajoutez le prénom si nécessaire

            fetch(`/api/uploadAvatar?id=${vendeurId}&nom=${updatedData.nom}`, {
                method: "POST",
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        document.getElementById("avatar").src = `/avatars/${updatedData.nom.toLowerCase()}.png?${new Date().getTime()}`;
                    } else {
                        alert("Erreur lors de la mise à jour de la photo de profil.");
                    }
                })
                .catch(error => console.error("Erreur lors de l'upload de la photo de profil:", error));
        }

        fetch(`/api/updateVendeur/${vendeurId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedData)
        })
            .then(response => {
                if (response.ok) {
                    alert("Profil mis à jour avec succès !");
                    initialData = updatedData;
                    toggleEditMode(false);
                } else {
                    alert("Erreur lors de la mise à jour du profil.");
                }
            })
            .catch(error => console.error("Erreur lors de la mise à jour du profil:", error));
    }
});