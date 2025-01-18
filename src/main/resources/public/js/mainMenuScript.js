document.addEventListener("DOMContentLoaded", function () {
    // Fetch low quantity articles
    fetch('/api/articles-lowQT')
        .then(response => response.json())
        .then(data => {
            const articleTable = document.querySelector('#low-article-table');
            articleTable.innerHTML = '';

            const headerRow = document.createElement('tr');
            headerRow.innerHTML = `
                <th>Nom du produit</th>
                <th>Quantité</th>
            `;
            articleTable.appendChild(headerRow);

            data.forEach(item => {
                const cleanedItem = item.replace(/[\[\]]/g, '');
                const [name, quantity] = cleanedItem.split(',').map(value => value.trim());

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${name}</td>
                    <td>${quantity}</td>
                `;
                articleTable.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching articles:', error));
});

document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/orders-waiting')
        .then(response => response.json())
        .then(data => {
            const articleTable = document.querySelector('#waiting-orders-table');
            articleTable.innerHTML = '';

            // Create a header row
            const headerRow = document.createElement('tr');
            headerRow.innerHTML = `
                <th>Nom du produit</th>
                <th>Quantité</th>
                <th>Date de commande</th>
                <th>Jours depuis commande</th>
            `;
            articleTable.appendChild(headerRow);

            // Populate table with parsed data
            data.forEach(item => {
                const { produit, quantite, dateCommande, joursDepuisCommande, mouvementStockId } = item;

                const row = document.createElement('tr');
                row.innerHTML = `
        <td>${produit}</td>
        <td>${quantite}</td>
        <td>${dateCommande}</td>
        <td>${joursDepuisCommande} jours</td>
    `;

                row.addEventListener('click', () => {
                    document.getElementById('productInfo').textContent = `Produit : ${produit}, Quantité prévue : ${quantite}`;
                    document.getElementById('receivedQuantity').value = quantite;
                    const confirmButton = document.getElementById('confirmOrderButton');
                    confirmButton.setAttribute('data-id', mouvementStockId);

                    const modal = new bootstrap.Modal(document.getElementById('orderModal'));
                    modal.show();
                });

                articleTable.appendChild(row);
            });


            // Handle confirmation
            document.getElementById('confirmOrderButton').addEventListener('click', function () {
                const id = this.getAttribute('data-id'); // Retrieve the movementStock ID from the button's attribute
                const quantiteRecu = document.getElementById('receivedQuantity').value;
                const dateRecu = document.getElementById('receivedDateCheckbox').checked
                    ? new Date().toISOString().split('T')[0] // Default to today
                    : document.getElementById('receivedDateInput').value; // Custom date

                fetch(`/api/orders-confirm?id=${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        id: id,
                        date: dateRecu,
                        quantite: quantiteRecu,
                    }),
                })
                    .then(response => {
                        if (response.ok) {
                            alert('Commande mise à jour avec succès avec les informations suivantes :\n' +
                                `Date de réception : ${dateRecu}\nQuantité reçue : ${quantiteRecu}\n` + `id : ${id}`);

                            location.reload(); // Refresh the page to show updated data
                        } else {
                            return response.text().then(text => {
                                throw new Error(text);
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Erreur lors de la mise à jour de la commande :', error);
                    });
            });

        })
        .catch(error => {
            console.error('Error fetching orders:', error);
            const articleTable = document.querySelector('#waiting-orders-table');
            articleTable.innerHTML = '<tr><td colspan="4">Erreur lors de la récupération des commandes.</td></tr>';
        });
});


document.addEventListener("DOMContentLoaded", () => {
    const todayCheckbox = document.getElementById('receivedDateCheckbox'); // Ensure correct ID
    const customDateContainer = document.getElementById('customDateContainer');
    const customDateInput = document.getElementById('receivedDateInput'); // Ensure correct ID

    // Get today's date in YYYY-MM-DD format
    const today = new Date().toISOString().split('T')[0];

    // Set the default date and max date to today
    customDateInput.value = today;
    customDateInput.setAttribute('max', today); // Prevent selecting future dates

    // Toggle visibility of custom date input based on checkbox
    todayCheckbox.addEventListener('change', () => {
        if (todayCheckbox.checked) {
            customDateContainer.style.display = 'none';
            customDateInput.value = today; // Reset to today's date
        } else {
            customDateContainer.style.display = 'block';
        }
    });

    // Ensure selected date is not in the future
    customDateInput.addEventListener('input', () => {
        if (customDateInput.value > today) {
            alert("Vous ne pouvez pas sélectionner une date future !");
            customDateInput.value = today; // Reset to today if invalid
        }
    });
});


document.addEventListener("DOMContentLoaded", function () {
    // Select the modal elements
    const orderModal = document.getElementById("orderModal");
    const closeModalButtons = document.querySelectorAll(".close-modal"); // Select elements with class "close-modal"

    // Add event listeners to close buttons (cross and "Annuler" button)
    closeModalButtons.forEach(button => {
        button.addEventListener("click", () => {
            const modalInstance = bootstrap.Modal.getInstance(orderModal);
            modalInstance.hide();
        });
    });
});
