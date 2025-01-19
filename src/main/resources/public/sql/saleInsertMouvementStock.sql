INSERT INTO MouvementStock (idMagasin, idProduit, volume, recipient, quantite, date)
VALUES (?, ?, ?, ?::typeRecipient, ?, ?) RETURNING id;