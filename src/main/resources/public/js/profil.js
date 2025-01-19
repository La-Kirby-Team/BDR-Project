document.addEventListener("DOMContentLoaded", function () {
    let initialData = {};

    Promise.all([fetchMagasins(), fetchVendeur(1)]);

    const editButton = document.getElementById("edit-button");
    const cancelButton = document.getElementById("cancel-button");
    const form = document.getElementById("profile-form");
    const editButtons = document.getElementById("edit-buttons");
    const avatarContainer = document.getElementById("upload-avatar-container");
    const avatarUpload = document.getElementById("avatar-upload");
    const fields = form.querySelectorAll("input, select");

    editButton.addEventListener("click", () => toggleEditMode(true));
    cancelButton.addEventListener("click", () => {
        toggleEditMode(false);
        resetFormFields();
    });

    form.addEventListener("submit", (e) => {
        e.preventDefault();
        saveProfile();
    });

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

                /*const avatarPath = `/avatars/${vendeur.nom.toLowerCase().replace(' ', '_')}.png?timestamp=${Date.now()}`;
                const avatarElement = document.getElementById("avatar");
                avatarElement.src = avatarPath;*/

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
            idMagasin: parseInt(document.getElementById("idMagasin").value)
        };

        const file = avatarUpload.files[0];
        if (file) {
            const formData = new FormData();
            formData.append("avatar", file);
            formData.append("nom", updatedData.nom);

            fetch(`/api/uploadAvatar?id=1&nom=${updatedData.nom}`, {
                method: "POST",
                body: formData
            })
                .catch(() => alert("Erreur lors de la mise à jour de la photo de profil."));
        }

        fetch("/api/updateVendeur/1", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(updatedData)
        })
            .then(response => {
                if (response.ok) {
                    toggleEditMode(false);
                } else {
                    alert("Erreur lors de la mise à jour du profil.");
                }
            })
            .catch(() => alert("Erreur lors de la mise à jour du profil."));
    }
});
