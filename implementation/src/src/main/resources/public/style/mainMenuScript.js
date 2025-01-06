document.addEventListener("DOMContentLoaded", function () {
    // Fetch data from the API
    fetch('/api/articles')
        .then(response => response.json())
        .then(data => {
            // Find the container for dynamic content
            const articleTable = document.querySelector('#article-table');

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
