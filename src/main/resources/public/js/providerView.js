// providerView.js - Updated search functionality and table rendering

document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/providers')
        .then(response => response.json())
        .then(data => {
            const providerTable = document.querySelector('#provider-table tbody');
            const searchInput = document.querySelector('#search-provider');
            providerTable.innerHTML = '';

            function renderTable(filteredData) {
                providerTable.innerHTML = '';

                filteredData.forEach(provider => {
                    // Ensure we are accessing the correct object properties
                    const { id, nom, adresse, numeroTelephone } = provider;

                    // Handle null or undefined values gracefully
                    const formattedID = id ?? 'Non défini';
                    const formattedName = nom ?? 'Nom inconnu';
                    const formattedAddress = adresse ?? 'Adresse inconnue';
                    const formattedPhone = numeroTelephone ?? 'Non spécifié';

                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${formattedName}</td>
                        <td>${formattedAddress}</td>
                        <td>${formattedPhone}</td>
                    `;

                    providerTable.appendChild(row);
                });
            }

            renderTable(data);

            // Search function
            searchInput.addEventListener('input', function () {
                const searchValue = searchInput.value.toLowerCase();
                const filteredData = data.filter(provider => {
                    return provider.nom.toLowerCase().includes(searchValue) ||
                        provider.adresse.toLowerCase().includes(searchValue) ||
                        provider.numeroTelephone.toLowerCase().includes(searchValue);
                });
                renderTable(filteredData);
            });

            // Redirect to providerView on button click
            document.getElementById('add-provider-button').addEventListener('click', function () {
                window.location.href = "/add-provider";
            });
        })
        .catch(error => console.error('Error fetching providers:', error));
});
