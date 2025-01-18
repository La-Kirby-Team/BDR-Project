document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/providers')
        .then(response => response.json())
        .then(data => {
            const providerTable = document.querySelector('#provider-table tbody');
            const searchInput = document.querySelector('#search-provider');
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteProviderModal'));
            let selectedProviderId = null;

            providerTable.innerHTML = '';

            function renderTable(filteredData) {
                providerTable.innerHTML = '';

                filteredData.forEach(provider => {
                    const { id, nom, adresse, numeroTelephone } = provider;

                    const formattedName = nom ?? 'Nom inconnu';
                    const formattedAddress = adresse ?? 'Adresse inconnue';
                    const formattedPhone = numeroTelephone ?? 'Non spécifié';

                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${formattedName}</td>
                        <td>${formattedAddress}</td>
                        <td>${formattedPhone}</td>
                    `;

                    // Attach click event to show delete confirmation modal
                    row.addEventListener('click', () => {
                        selectedProviderId = id;
                        deleteModal.show();
                    });

                    providerTable.appendChild(row);
                });
            }

            renderTable(data);

            // Search function
            searchInput.addEventListener('input', function () {
                const searchValue = searchInput.value.toLowerCase();
                const filteredData = data.filter(provider =>
                    provider.nom.toLowerCase().includes(searchValue) ||
                    provider.adresse.toLowerCase().includes(searchValue) ||
                    provider.numeroTelephone.toLowerCase().includes(searchValue)
                );
                renderTable(filteredData);
            });

            // Handle delete confirmation
            document.getElementById('confirmDeleteButton').addEventListener('click', function () {
                if (selectedProviderId) {
                    fetch(`/api/providers/${selectedProviderId}`, {
                        method: 'DELETE'
                    })
                        .then(response => {
                            if (!response.ok) throw new Error("Erreur lors de la suppression");
                            return response.text();
                        })
                        .then(() => {
                            deleteModal.hide();
                            alert("Fournisseur supprimé avec succès !");
                            location.reload(); // Refresh table
                        })
                        .catch(error => console.error("Erreur lors de la suppression :", error));
                }
            });

            // Redirect to providerView on button click
            document.getElementById('add-provider-button').addEventListener('click', function () {
                window.location.href = "/add-provider";
            });
        })
        .catch(error => console.error('Error fetching providers:', error));
});
