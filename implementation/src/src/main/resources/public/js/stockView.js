document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/stock')
        .then(response => response.json())
        .then(data => {
            const stockTable = document.querySelector('#stock-table tbody');
            const searchInput = document.querySelector('#search-stock');
            stockTable.innerHTML = '';

            function renderTable(filteredData) {
                stockTable.innerHTML = '';

                filteredData.forEach(item => {
                    let values = item.replace(/[\[\]"]/g, '').split(',').map(value => value.trim());

                    if (!values || values.length !== 5) {  // Expecting 5 columns including ID
                        console.error("Incorrect data format:", item);
                        return;
                    }

                    let [id, name, volume, recipient, quantity] = values;

                    let formattedQuantity = (quantity === 'null' || quantity === '') ? 'Non disponible' : parseInt(quantity);
                    const quantityDisplay = formattedQuantity < 5
                        ? `<span style="color: red;">${formattedQuantity}</span>`
                        : formattedQuantity;

                    const row = document.createElement('tr');
                    row.innerHTML = `
            <td>${name}</td>
            <td>${volume}</td>
            <td>${recipient}</td>
            <td>${quantityDisplay}</td>
        `;

                    // row.addEventListener('click', () => {
                    //     console.log("Clicked row:", values);  // Debugging
                    //
                    //     let id = values[0];  // Ensure ID is correctly extracted
                    //     id = id.replace(/[^0-9]/g, '');  // Remove any unwanted characters
                    //     if (!id || isNaN(parseInt(id))) {
                    //         console.error("Invalid ID detected:", id);
                    //         return;
                    //     }
                    //
                    //     row.style.cursor = "pointer";
                    //     row.addEventListener("mouseenter", () => row.style.backgroundColor = "rgba(0, 123, 255, 0.2)");
                    //     row.addEventListener("mouseleave", () => row.style.backgroundColor = "");
                    //
                    //     document.getElementById('modalProductName').textContent = name;
                    //     document.getElementById('modalVolume').value = volume;
                    //     document.getElementById('modalRecipient').value = recipient;
                    //     document.getElementById('modalQuantity').value = formattedQuantity === 'Non disponible' ? '' : formattedQuantity;
                    //
                    //
                    //     // Ensure only quantity is editable
                    //     document.getElementById('modalVolume').readOnly = true;
                    //     document.getElementById('modalRecipient').readOnly = true;
                    //     document.getElementById('modalQuantity').readOnly = false;
                    //
                    //     const confirmButton = document.getElementById('confirmUpdateButton');
                    //     confirmButton.setAttribute('data-id', id);  // ✅ Correctly store ID
                    //     confirmButton.setAttribute('data-name', name);
                    //     confirmButton.setAttribute('data-volume', volume);
                    //     confirmButton.setAttribute('data-recipient', recipient);
                    //     confirmButton.setAttribute('data-original', quantity);
                    //
                    //     const modal = new bootstrap.Modal(document.getElementById('editStockModal'));
                    //     modal.show();
                    // });

                    row.addEventListener("mousedown", () => {
                        row.style.backgroundColor = "rgba(0, 123, 255, 0.4)";
                    });
                    row.addEventListener("mouseup", () => {
                        setTimeout(() => { row.style.backgroundColor = ""; }, 200);
                    });

                    stockTable.appendChild(row);
                });
            }


            renderTable(data);

            // Search function
            searchInput.addEventListener('input', function () {
                const searchValue = searchInput.value.toLowerCase();
                const filteredData = data.filter(item => {
                    const values = item.replace(/[\[\]"]/g, '').split(',').map(value => value.trim());
                    return values.some(value => value.toLowerCase().includes(searchValue));
                });
                renderTable(filteredData);
            });
        })
        .catch(error => console.error('Error fetching stock:', error));
});

// Handle stock update confirmation
document.getElementById('confirmUpdateButton').addEventListener('click', function () {
    console.log("Updating stock...");
    let id = this.getAttribute('data-id');

    if (!id || isNaN(parseInt(id))) {
        console.error("Invalid ID detected:", id);
        alert("Erreur: ID du stock invalide.");
        return;
    }

    let updatedQuantity = parseInt(document.getElementById('modalQuantity').value);

    fetch(`/api/update-stock`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            id: id,  // ✅ Ensure ID is properly sent
            quantity: updatedQuantity
        })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            // return response.json();
        })
        .then(result => {
            console.log("Stock updated successfully", result);
            location.reload();
        })
        .catch(error => {
            console.error("Error updating stock:", error);
            alert("Erreur lors de la mise à jour du stock: " + error.message);
        });
});

