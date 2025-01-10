document.addEventListener("DOMContentLoaded", function () {
    // Fetch data from the API
    fetch('/api/articles-lowQT')
        .then(response => response.json())
        .then(data => {
            // Find the container for dynamic content
            const articleTable = document.querySelector('#low-article-table');

            // Clear any existing content
            articleTable.innerHTML = '';

            // Create a header row
            const headerRow = document.createElement('tr');
            headerRow.innerHTML = `
                <th>Nom du produit</th>
                <th>Quantité</th>
            `;
            articleTable.appendChild(headerRow);

            // Populate table with parsed data
            data.forEach(item => {
                // Remove square brackets if present and split the string
                const cleanedItem = item.replace(/[\[\]]/g, ''); // Remove brackets
                const [name, quantity] = cleanedItem.split(',').map(value => value.trim());

                // Create a new table row
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
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const articleTable = document.querySelector('#waiting-orders-table');
            articleTable.innerHTML = '';

            const headerRow = document.createElement('tr');
            headerRow.innerHTML = `
                <th>Nom du produit</th>
                <th>Quantité</th>
                <th>Date de commande</th>
                <th>Jours depuis commande</th>
            `;
            articleTable.appendChild(headerRow);

            data.forEach(item => {
                const [name, quantity, orderDate, daysSince] = item
                    .replace(/[\[\]]/g, '')
                    .split(',')
                    .map(value => value.trim());

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${name}</td>
                    <td>${quantity}</td>
                    <td>${orderDate}</td>
                    <td>${daysSince} jours</td>
                `;

                // Add click event to show modal
                row.addEventListener('click', () => {
                    // Populate modal with data
                    document.getElementById('orderDetails').textContent = `Produit : ${name}, Quantité prévue : ${quantity}`;
                    document.getElementById('adjustQuantity').value = quantity;

                    // Store current order details for confirmation
                    document.getElementById('confirmOrderBtn').dataset.name = name;

                    // Show the modal
                    const orderModal = new bootstrap.Modal(document.getElementById('orderModal'));
                    orderModal.show();
                });

                articleTable.appendChild(row);
            });

            // Handle confirmation
            document.getElementById('confirmOrderBtn').addEventListener('click', () => {
                const adjustedQuantity = document.getElementById('adjustQuantity').value;
                const productName = document.getElementById('confirmOrderBtn').dataset.name;

                // Perform an API call to update the order (POST/PUT request)
                fetch('/api/orders-confirm', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        name: productName,
                        adjustedQuantity: adjustedQuantity
                    })
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Error confirming the order');
                        }
                        return response.json();
                    })
                    .then(() => {
                        alert('Commande confirmée avec succès!');
                        location.reload(); // Refresh the table after confirmation
                    })
                    .catch(error => {
                        console.error('Error confirming the order:', error);
                        alert('Une erreur s\'est produite lors de la confirmation.');
                    });
            });
        })
        .catch(error => {
            console.error('Error fetching orders:', error);
            const articleTable = document.querySelector('#waiting-orders-table');
            articleTable.innerHTML = '<tr><td colspan="4">Erreur lors de la récupération des commandes.</td></tr>';
        });
});

