document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/stock')
        .then(response => response.json())
        .then(data => {
            const stockTable = document.querySelector('#stock-table');
            const searchInput = document.querySelector('#search-stock'); // Search input field
            stockTable.innerHTML = '';

            // Create a header row with all necessary columns
            const headerRow = document.createElement('tr');
            headerRow.innerHTML = `
                <th>Nom du produit</th>
                <th>Volume</th>
                <th>Recipient</th>
                <th>Quantité</th>
            `;
            stockTable.appendChild(headerRow);

            // Function to render the stock table
            function renderTable(filteredData) {
                stockTable.innerHTML = ''; // Clear the table before re-rendering

                // Append the header again
                stockTable.appendChild(headerRow);

                filteredData.forEach(item => {
                    const [name, volume, recipient, quantity] = item
                        .replace(/[\[\]"]/g, '') // Remove brackets and quotes
                        .split(',')
                        .map(value => value.trim());

                    // Handle NULL values for quantity
                    let formattedQuantity = quantity === 'NULL' ? 'Non disponible' : quantity;

                    // Apply formatting for values
                    let quantityDisplay;
                    if (formattedQuantity === 'Non disponible') {
                        quantityDisplay = `<span style="color: gray;">${formattedQuantity}</span>`; // Gray for unavailable values
                    } else {
                        const quantityValue = parseInt(formattedQuantity, 10);
                        if (quantityValue < 5) {
                            quantityDisplay = `<span style="color: red; font-weight: bold;">${formattedQuantity}</span>`; // Red for low stock
                        } else {
                            quantityDisplay = formattedQuantity; // Default display
                        }
                    }

                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${name}</td>
                        <td>${volume}</td>
                        <td>${recipient}</td>
                        <td>${quantityDisplay}</td>
                    `;

                    stockTable.appendChild(row);
                });
            }

            // Initial render
            renderTable(data);

            // Event listener for search
            searchInput.addEventListener('input', function () {
                const searchValue = searchInput.value.toLowerCase();
                const filteredData = data.filter(item => {
                    const [name, volume, recipient, quantity] = item
                        .replace(/[\[\]"]/g, '') // Remove brackets and quotes
                        .split(',')
                        .map(value => value.trim());

                    return name.toLowerCase().includes(searchValue) ||
                        volume.includes(searchValue) ||
                        recipient.toLowerCase().includes(searchValue) ||
                        quantity.includes(searchValue);
                });

                renderTable(filteredData);
            });
        })
        .catch(error => console.error('Error fetching stock:', error));
});
